package com.github.platan.idea.dependencies.maven

import static MavenDependencyBuilder.aMavenDependency

import com.github.platan.idea.dependencies.gradle.Dependency
import com.github.platan.idea.dependencies.gradle.Exclusion
import spock.lang.Specification

class MavenToGradleMapperTest extends Specification {

    def 'map maven dependency to gradle dependency'() {
        given:
        MavenToGradleMapper mavenToGradleMapper = new MavenToGradleMapperImpl()
        def dependency = aMavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4')
                .withExclusionList([new MavenExclusion('org.codehaus.groovy', 'groovy-all')]).build()

        expect:
        mavenToGradleMapper.map(dependency) == new Dependency('org.spockframework', 'spock-core', '1.0-groovy-2.4', 'compile',
                [new Exclusion('org.codehaus.groovy', 'groovy-all')])
    }

    def 'map maven dependency with wildcard exclusion to gradle non transitive dependency'() {
        given:
        MavenToGradleMapper mavenToGradleMapper = new MavenToGradleMapperImpl()
        def mavenDependency = aMavenDependency('org.spockframework', 'spock-core', '1.0-groovy-2.4')
                .withExclusionList([new MavenExclusion('*', '*')])

        when:
        def dependency = mavenToGradleMapper.map(mavenDependency.build())

        then:
        !dependency.transitive
        !dependency.exclusions
    }

}
