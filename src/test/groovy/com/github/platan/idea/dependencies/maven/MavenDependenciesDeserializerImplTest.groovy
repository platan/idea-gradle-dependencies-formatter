package com.github.platan.idea.dependencies.maven

import spock.lang.Specification
import spock.lang.Unroll

class MavenDependenciesDeserializerImplTest extends Specification {

    private MavenDependenciesDeserializer mavenDependencyParser = new MavenDependenciesDeserializerImpl()

    def 'parse one dependency with default scope'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
</dependency>"""

        when:
        def dependencies = mavenDependencyParser.deserialize(mavenDependency)

        then:
        dependencies == [new MavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4', Scope.COMPILE)]
    }

    def 'parse one dependency with exclusions'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
    <exclusions>
      <exclusion>
        <groupId>org.codehaus.groovy</groupId>
        <artifactId>groovy-all</artifactId>
      </exclusion>
    </exclusions>
</dependency>"""

        when:
        def dependencies = mavenDependencyParser.deserialize(mavenDependency)

        then:
        dependencies == [new MavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4',
                [new MavenExclusion('org.codehaus.groovy', 'groovy-all')])]
    }

    @Unroll
    def 'parse one dependency with scope #scope'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
    <scope>${scope}</scope>
</dependency>"""

        when:
        def dependencies = mavenDependencyParser.deserialize(mavenDependency)

        then:
        dependencies == [new MavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4', expextedScope)]

        where:
        scope      || expextedScope
        'compile'  || Scope.COMPILE
        'provided' || Scope.PROVIDED
        'runtime'  || Scope.RUNTIME
        'test'     || Scope.TEST
        'system'   || Scope.SYSTEM
    }

    def 'throw exception for dependency with invalid scope'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
    <scope>_unsupported_</scope>
</dependency>"""

        when:
        mavenDependencyParser.deserialize(mavenDependency)

        then:
        thrown Exception
    }

    def 'throw exception for dependency with duplicated groupId'() {
        given:
        def mavenDependency = """<dependency>
    <groupId>org.spockframework</groupId>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
</dependency>"""

        when:
        mavenDependencyParser.deserialize(mavenDependency)

        then:
        thrown DependencyValidationException
    }

    def 'throw exception for dependency without groupId'() {
        given:
        def mavenDependency = """<dependency>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
</dependency>"""

        when:
        mavenDependencyParser.deserialize(mavenDependency)

        then:
        thrown DependencyValidationException
    }

    def 'parse two dependencies'() {
        given:
        def mavenDependencies = """<dependency>
    <groupId>org.spockframework</groupId>
    <artifactId>spock-core</artifactId>
    <version>1.0-groovy-2.4</version>
</dependency>
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>2.3.11</version>
</dependency>"""

        when:
        def dependencies = mavenDependencyParser.deserialize(mavenDependencies)

        then:
        dependencies == [new MavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4'),
                         new MavenDependency('org.codehaus.groovy', 'groovy-all', '2.3.11')]
    }

    def 'parse two dependencies nested in dependencies element'() {
        given:
        def mavenDependencies = """<dependencies>
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

        when:
        def dependencies = mavenDependencyParser.deserialize(mavenDependencies)

        then:
        dependencies == [new MavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4'),
                         new MavenDependency('org.codehaus.groovy', 'groovy-all', '2.3.11')]
    }

}
