package com.github.platan.idea.dependencies.maven

import com.github.platan.idea.dependencies.gradle.Dependency
import com.github.platan.idea.dependencies.gradle.Exclusion
import spock.lang.Specification

class MavenToGradleMapperTest extends Specification {

    def 'map maven dependency to gradle dependency'() {
        given:
        MavenToGradleMapper mavenToGradleMapper = new MavenToGradleMapperImpl()
        def dependency = new MavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4',
                [new MavenExclusion('org.codehaus.groovy', 'groovy-all')])

        expect:
        mavenToGradleMapper.map(dependency) == new Dependency('org.spockframework', 'spock-core', '1.0-groovy-2.4', 'compile',
                [new Exclusion('org.codehaus.groovy', 'groovy-all')])
    }

    def 'map maven dependency with wildcard exclusion to gradle non transitive dependency'() {
        given:
        MavenToGradleMapper mavenToGradleMapper = new MavenToGradleMapperImpl()
        def mavenDependency = new MavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4',
                [new MavenExclusion('*', '*')])

        when:
        def dependency = mavenToGradleMapper.map(mavenDependency)

        then:
        !dependency.transitive
        !dependency.exclusions
    }

}
