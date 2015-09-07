package com.github.platan.idea.dependencies

import com.github.platan.idea.dependencies.gradle.GradleDependenciesSerializerImpl
import com.github.platan.idea.dependencies.maven.MavenDependenciesDeserializerImpl
import com.github.platan.idea.dependencies.maven.MavenToGradleMapperImpl
import spock.lang.Specification

class MavenToGradleConverterTest extends Specification {

    private MavenToGradleConverter mavenToGradleConverter = new MavenToGradleConverter(new MavenDependenciesDeserializerImpl(),
            new GradleDependenciesSerializerImpl(), new MavenToGradleMapperImpl())

    def 'xml with one dependency to gradle dsl'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
</dependency>
"""
        expect:
        mavenToGradleConverter.convert(mavenDependency) == "compile 'org.spockframework:spock-core:1.0-groovy-2.4'"
    }

    def 'convert maven dependency with variable for version'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>\${version.spock}</version>
</dependency>
"""
        expect:
        mavenToGradleConverter.convert(mavenDependency) == "compile 'org.spockframework:spock-core:\${version.spock}'"
    }

    def 'convert maven dependency without version'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
</dependency>
"""
        expect:
        mavenToGradleConverter.convert(mavenDependency) == "compile 'org.spockframework:spock-core'"
    }

    def 'convert maven dependency with classifier'() {
        given:
        def mavenDependency = """<dependency>
  <groupId>com.carrotsearch</groupId>
  <artifactId>hppc</artifactId>
  <version>0.5.4</version>
  <classifier>jdk15</classifier>
</dependency>
"""
        expect:
        mavenToGradleConverter.convert(mavenDependency) == "compile 'com.carrotsearch:hppc:0.5.4:jdk15'"
    }

    def 'convert maven dependency with optional'() {
        given:
        def mavenDependency = """<dependency>
  <groupId>joda-time</groupId>
  <artifactId>joda-time</artifactId>
  <version>2.8</version>
  <optional>true</optional>
</dependency>
"""
        expect:
        mavenToGradleConverter.convert(mavenDependency) == "compile 'joda-time:joda-time:2.8', optional"
    }

    def 'convert maven dependency with optional and with closure'() {
        given:
        def mavenDependency = """<dependency>
      <groupId>joda-time</groupId>
      <artifactId>joda-time</artifactId>
      <version>2.8</version>
      <optional>true</optional>
      <exclusions>
        <exclusion>
          <groupId>joda-time</groupId>
          <artifactId>joda-convert</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
    """
        expect:
        mavenToGradleConverter.convert(mavenDependency) == """compile('joda-time:joda-time:2.8') """ +
                """{ // optional = true (optional is not supported for dependency with closure)
\texclude group: 'joda-time', module: 'joda-convert'
}"""
    }

    def 'maven dependency with systemPath is not supported yet'() {
        given:
        def mavenDependency = """<dependency>
            <groupId>info.cukes</groupId>
            <artifactId>gherkin</artifactId>
            <version>2.12.2</version>
            <scope>system</scope>
            <systemPath>\${project.basedir}/lib/gherkin-2.12.2.jar</systemPath>
        </dependency>"""

        expect:
        mavenToGradleConverter.convert(mavenDependency) == "system 'info.cukes:gherkin:2.12.2' " +
                "// systemPath = \${project.basedir}/lib/gherkin-2.12.2.jar (systemPath is not supported)"
    }

    def 'maven dependency with two unsupported elements'() {
        given:
        def mavenDependency = """<dependency>
            <groupId>info.cukes</groupId>
            <artifactId>gherkin</artifactId>
            <version>2.12.2</version>
            <scope>system</scope>
            <systemPath>\${project.basedir}/lib/gherkin-2.12.2.jar</systemPath>
            <type>jar</type>
        </dependency>"""

        expect:
        mavenToGradleConverter.convert(mavenDependency) == "system 'info.cukes:gherkin:2.12.2' " +
                "// systemPath = \${project.basedir}/lib/gherkin-2.12.2.jar (systemPath is not supported), " +
                "type = jar (type is not supported)"
    }

    def 'maven dependency with type is not supported yet'() {
        given:
        def mavenDependency = """<dependency>
            <groupId>org.apache.maven</groupId>
            <artifactId>apache-maven</artifactId>
            <version>3.0.1</version>
            <classifier>bin</classifier>
            <type>zip</type>
        </dependency>"""

        expect:
        mavenToGradleConverter.convert(mavenDependency) == "compile 'org.apache.maven:apache-maven:3.0.1:bin' " +
                "// type = zip (type is not supported)"
    }

    def 'skip comments during conversion'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
</dependency>
<!--<dependency>-->
    <!--<groupId>junit</groupId>-->
    <!--<artifactId>junit</artifactId>-->
    <!--<version>4.8.2</version>-->
    <!--<scope>test</scope>-->
<!--</dependency>-->
"""
        expect:
        mavenToGradleConverter.convert(mavenDependency) == "compile 'org.spockframework:spock-core:1.0-groovy-2.4'"
    }

    def 'xml with one test dependency to gradle dsl'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
    <scope>test</scope>
</dependency>
"""
        expect:
        mavenToGradleConverter.convert(mavenDependency) == "testCompile 'org.spockframework:spock-core:1.0-groovy-2.4'"
    }

    def 'XML with maven dependency with exclusions to gradle dependency'() {
        given:
        def dependency = """    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-embedder</artifactId>
      <version>2.0</version>
      <exclusions>
        <exclusion>
          <groupId>org.apache.maven</groupId>
          <artifactId>maven-core</artifactId>
        </exclusion>
      </exclusions>
    </dependency>"""

        expect:
        mavenToGradleConverter.convert(dependency) == """compile('org.apache.maven:maven-embedder:2.0') {
\texclude group: 'org.apache.maven', module: 'maven-core'
}"""
    }

    def 'xml with two dependencies to gradle dsl'() {
        given:
        def mavenDependency = """<dependencies>
    <dependency>
        <groupId>org.spockframework</groupId>
        <artifactId>spock-core</artifactId>
        <version>1.0-groovy-2.4</version>
    </dependency>
    <dependency>
        <groupId>org.codehaus.groovy</groupId>
        <artifactId>groovy-all</artifactId>
        <version>2.3.11</version>
    </dependency>
</dependencies>"""

        expect:
        mavenToGradleConverter.convert(mavenDependency) == """compile 'org.spockframework:spock-core:1.0-groovy-2.4'
compile 'org.codehaus.groovy:groovy-all:2.3.11'"""
    }

    def 'return original text if it cannot be converted because is not a well formed XML'() {
        given:
        def givenNotWellFormedDocument = """<dependency>
        <groupId>org.spockframework"""

        when:
        def converted = mavenToGradleConverter.convert(givenNotWellFormedDocument)

        then:
        converted == givenNotWellFormedDocument
    }

    def 'return original text if it cannot be converted because is a plain text'() {
        given:
        def plainText = 'org.spockframework'

        when:
        def converted = mavenToGradleConverter.convert(plainText)

        then:
        converted == plainText
    }
}
