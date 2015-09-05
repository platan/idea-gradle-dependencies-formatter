package com.github.platan.idea.dependencies.gradle

import static com.github.platan.idea.dependencies.gradle.DependencyBuilder.aDependency

import spock.lang.Specification

class GradleDependenciesSerializerImplTest extends Specification {

    def 'serialize dependency with exclusion'() {
        given:
        GradleDependenciesSerializer gradleDependenciesSerializer = new GradleDependenciesSerializerImpl()

        when:
        def serialized = gradleDependenciesSerializer.serialize([aDependency('org.spockframework', 'spock-core')
                                                                         .withVersion('1.0-groovy-2.4')
                                                                         .withConfiguration('compile')
                                                                         .withExclusions([new Exclusion('org.codehaus.groovy',
                'groovy-all')]).build()])

        then:
        serialized == """compile('org.spockframework:spock-core:1.0-groovy-2.4') {
\texclude group: 'org.codehaus.groovy', module: 'groovy-all'
}"""
    }

    def 'serialize dependency with wildcard exclusion'() {
        given:
        GradleDependenciesSerializer gradleDependenciesSerializer = new GradleDependenciesSerializerImpl()

        when:
        def serialized = gradleDependenciesSerializer.serialize([aDependency('org.spockframework', 'spock-core')
                                                                         .withVersion('1.0-groovy-2.4')
                                                                         .withConfiguration('compile')
                                                                         .withTransitive(false).build()])

        then:
        serialized == """compile('org.spockframework:spock-core:1.0-groovy-2.4') {
\ttransitive = false
}"""
    }

}
