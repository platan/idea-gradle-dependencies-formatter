package com.github.platan.idea.dependencies.gradle

import spock.lang.Specification

class CoordinateComparatorTest extends Specification {

    def "compare by group first"() {
        expect:
        Coordinate.parse('a:b') == Coordinate.parse('a:b')

        and:
        Coordinate.parse('a:b') < Coordinate.parse('c:b')

        and:
        Coordinate.parse('c:b') > Coordinate.parse('a:b')
    }

    def "compare by name after group"() {
        expect:
        Coordinate.parse('a:b') == Coordinate.parse('a:b')

        and:
        Coordinate.parse('a:b') < Coordinate.parse('a:c')

        and:
        Coordinate.parse('a:c') > Coordinate.parse('a:b')
    }
}
