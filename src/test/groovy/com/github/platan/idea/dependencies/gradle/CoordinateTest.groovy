package com.github.platan.idea.dependencies.gradle

import com.google.common.base.Optional
import spock.lang.Specification
import spock.lang.Unroll

class CoordinateTest extends Specification {

    def 'parse coordinate with group, name and version'() {
        given:
        def stringCoordinate = 'com.google.guava:guava:18.0'

        when:
        def coordinate = Coordinate.parse(stringCoordinate)

        then:
        with(coordinate) {
            group == Optional.of('com.google.guava')
            name == 'guava'
            version == Optional.of('18.0')
        }
    }

    def 'parse coordinate with group and name'() {
        given:
        def stringCoordinate = 'com.google.guava:guava'

        when:
        def coordinate = Coordinate.parse(stringCoordinate)

        then:
        with(coordinate) {
            group == Optional.of('com.google.guava')
            name == 'guava'
        }
    }

    def 'parse coordinate with name'() {
        given:
        def stringCoordinate = 'guava'

        when:
        def coordinate = Coordinate.parse(stringCoordinate)

        then:
        with(coordinate) {
            name == 'guava'
        }
    }

    def 'parse coordinate with group, name, version, classifier and extension'() {
        given:
        def stringCoordinate = 'com.google.guava:guava:18.0:sources@jar'

        when:
        def coordinate = Coordinate.parse(stringCoordinate)

        then:
        with(coordinate) {
            group == Optional.of('com.google.guava')
            name == 'guava'
            version == Optional.of('18.0')
            classifier == Optional.of('sources')
            extension == Optional.of('jar')
        }
    }

    def 'parse coordinate with group, name, version and classifier'() {
        given:
        def stringCoordinate = 'com.google.guava:guava:18.0:sources'

        when:
        def coordinate = Coordinate.parse(stringCoordinate)

        then:
        with(coordinate) {
            group == Optional.of('com.google.guava')
            name == 'guava'
            version == Optional.of('18.0')
            classifier == Optional.of('sources')
        }
    }

    @Unroll
    def 'cannot create coordinate from empty string representation'() {
        when:
        Coordinate.parse(stringCoordinate)

        then:
        thrown IllegalArgumentException

        where:
        stringCoordinate << ['', ' ', ':']
    }

    def 'cannot create coordinate from string representation with empty element'() {
        when:
        Coordinate.parse('com.google.guava:guava: ')

        then:
        thrown IllegalArgumentException
    }

    @Unroll
    def '#dependency is parsable'() {
        expect:
        Coordinate.isStringNotationCoordinate(dependency)

        where:
        dependency                                | _
        'com.google.guava:guava'                  | _
        'com.google.guava:guava:18.0'             | _
        'com.google.guava:guava:18.0:sources'     | _
        'com.google.guava:guava::sources'         | _
        'com.google.guava:guava:18.0:sources@jar' | _
        'com.google.guava:guava:18.0:@jar'        | _
        'com.google.guava:guava::@jar'            | _
    }

    @Unroll
    def '#dependency is not parsable'() {
        expect:
        !Coordinate.isStringNotationCoordinate(dependency)

        where:
        dependency               | _
        ' '                      | _
        ':'                      | _
        'guava'                  | _
        'com.google.guava::18.0' | _
        'com.google.guava::'     | _
        '::18.0'                 | _
        ':guava:'                | _
    }

    def 'convert to map notation'() {
        given:
        def coordinate = Coordinate.CoordinateBuilder.aCoordinate('guava')
                .withGroup('com.google.guava')
                .withVersion('18.0')
                .withClassifier('sources')
                .withExtension('jar').build()

        expect:
        coordinate.toMapNotation("'") == "group: 'com.google.guava', name: 'guava', version: '18.0', classifier: 'sources', ext: 'jar'"
    }

}
