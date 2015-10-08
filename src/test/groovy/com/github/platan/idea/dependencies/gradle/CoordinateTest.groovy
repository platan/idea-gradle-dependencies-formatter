package com.github.platan.idea.dependencies.gradle

import static com.github.platan.idea.dependencies.gradle.Coordinate.CoordinateBuilder.aCoordinate

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
        def coordinate = aCoordinate('guava')
                .withGroup('com.google.guava')
                .withVersion('18.0')
                .withClassifier('sources')
                .withExtension('jar').build()

        expect:
        coordinate.toMapNotation("'") == "group: 'com.google.guava', name: 'guava', version: '18.0', classifier: 'sources', ext: 'jar'"
    }

    def "build from map"() {
        given:
        def coordinateMap = [group: 'com.google.guava', name: 'guava', version: '18.0', classifier: 'sources', ext: 'jar']

        when:
        def coordinate = Coordinate.fromMap(coordinateMap)

        then:
        with(coordinate) {
            name == 'guava'
            group == Optional.of('com.google.guava')
            version == Optional.of('18.0')
            classifier == Optional.of('sources')
            extension == Optional.of('jar')
        }
    }

    @Unroll
    def "format #coordinate to string notation #stringNotation"() {
        expect:
        coordinate.toStringNotation() == stringNotation

        where:
        coordinate                                                 || stringNotation
        aCoordinate('guava').withGroup('com.google.guava').build() || 'com.google.guava:guava'
        aCoordinate('guava')
                .withGroup('com.google.guava')
                .withVersion('18.0')
                .build()                                           || 'com.google.guava:guava:18.0'
        aCoordinate('guava')
                .withGroup('com.google.guava')
                .withVersion('18.0')
                .withClassifier('sources')
                .build()                                           || 'com.google.guava:guava:18.0:sources'
        aCoordinate('guava')
                .withGroup('com.google.guava')
                .withClassifier('sources')
                .build()                                           || 'com.google.guava:guava::sources'
        aCoordinate('guava')
                .withGroup('com.google.guava')
                .withVersion('18.0')
                .withClassifier('sources')
                .withExtension('jar')
                .build()                                           || 'com.google.guava:guava:18.0:sources@jar'
        aCoordinate('guava')
                .withGroup('com.google.guava')
                .withVersion('18.0')
                .withExtension('jar')
                .build()                                           || 'com.google.guava:guava:18.0:@jar'
        aCoordinate('guava')
                .withGroup('com.google.guava')
                .withExtension('jar')
                .build()                                           || 'com.google.guava:guava::@jar'
    }
}
