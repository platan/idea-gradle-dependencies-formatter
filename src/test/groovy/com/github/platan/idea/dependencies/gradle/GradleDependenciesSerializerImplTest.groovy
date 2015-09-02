package com.github.platan.idea.dependencies.gradle

import spock.lang.Specification

class GradleDependenciesSerializerImplTest extends Specification {

    def 'serialize dependency with exclusion'() {
        given:
        GradleDependenciesSerializer gradleDependenciesSerializer = new GradleDependenciesSerializerImpl()

        when:
        def serialized = gradleDependenciesSerializer.serialize([new Dependency('org.spockframework', 'spock-core', '1.0-groovy-2.4',
                'compile', [new Exclusion('org.codehaus.groovy', 'groovy-all')])])

        then:
        serialized == """compile('org.spockframework:spock-core:1.0-groovy-2.4') {
\texclude group: 'org.codehaus.groovy', module: 'groovy-all'
}"""
    }

    def 'serialize dependency with wildcard exclusion'() {
        given:
        GradleDependenciesSerializer gradleDependenciesSerializer = new GradleDependenciesSerializerImpl()

        when:
        def serialized = gradleDependenciesSerializer.serialize([new Dependency('org.spockframework', 'spock-core', '1.0-groovy-2.4',
                'compile', [], false)])

        then:
        serialized == """compile('org.spockframework:spock-core:1.0-groovy-2.4') {
\ttransitive = false
}"""
    }

}
