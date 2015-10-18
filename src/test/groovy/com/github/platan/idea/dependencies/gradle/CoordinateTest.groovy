package com.github.platan.idea.dependencies.gradle

import static com.github.platan.idea.dependencies.gradle.Coordinate.CoordinateBuilder.aCoordinate

import spock.lang.Specification
import spock.lang.Unroll

class CoordinateTest extends Specification {

    @Unroll
    def 'parse #stringNotation'() {
        expect:
        Coordinate.isStringNotationCoordinate(stringNotation)

        and:
        Coordinate.parse(stringNotation) == dependency

        where:
        stringNotation                            | dependency
        ':guava'                                  | aCoordinate('guava').build()
        ':guava:'                                 | aCoordinate('guava').build()
        'com.google.guava:guava'                  | aCoordinate('guava').withGroup('com.google.guava').build()
        'com.google.guava:guava:'                 | aCoordinate('guava').withGroup('com.google.guava').build()
        ':guava:18.0'                             | aCoordinate('guava').withVersion('18.0').build()
        'com.google.guava:guava:18.0'             | aCoordinate('guava').withGroup('com.google.guava')
                .withVersion('18.0').build()
        ':guava:18.0'                             | aCoordinate('guava').withVersion('18.0').build()
        'com.google.guava:guava:18.0:sources'     | aCoordinate('guava').withGroup('com.google.guava')
                .withVersion('18.0').withClassifier('sources').build()
        'com.google.guava:guava::sources'         | aCoordinate('guava').withGroup('com.google.guava')
                .withClassifier('sources').build()
        ':guava::sources'                         | aCoordinate('guava').withClassifier('sources').build()
        'com.google.guava:guava:18.0:sources@jar' | aCoordinate('guava').withGroup('com.google.guava')
                .withVersion('18.0').withClassifier('sources').withExtension('jar').build()
        'com.google.guava:guava:18.0:@jar'        | aCoordinate('guava').withGroup('com.google.guava')
                .withVersion('18.0').withExtension('jar').build()
        'com.google.guava:guava:18.0@jar'         | aCoordinate('guava').withGroup('com.google.guava')
                .withVersion('18.0').withExtension('jar').build()
        ':guava:18.0@jar'                         | aCoordinate('guava').withVersion('18.0').withExtension('jar').build()
        ':guava@jar'                              | aCoordinate('guava').withExtension('jar').build()
        ':guava:@jar'                             | aCoordinate('guava').withExtension('jar').build()
        ':guava:18.0:@jar'                        | aCoordinate('guava').withVersion('18.0').withExtension('jar').build()
        'com.google.guava:guava::@jar'            | aCoordinate('guava').withGroup('com.google.guava')
                .withExtension('jar').build()
    }

    @Unroll
    def 'cannot create coordinate from "#invalidStringCoordinate"'() {
        when:
        Coordinate.parse(invalidStringCoordinate)

        then:
        thrown IllegalArgumentException

        where:
        invalidStringCoordinate  | _
        ' '                      | _
        ':'                      | _
        'guava'                  | _
        'com.google.guava::18.0' | _
        'com.google.guava::'     | _
        '::18.0'                 | _
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

}
