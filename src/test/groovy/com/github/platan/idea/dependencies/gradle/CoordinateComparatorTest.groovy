package com.github.platan.idea.dependencies.gradle

import spock.lang.Specification

class CoordinateComparatorTest extends Specification {

    def "compare by group first"() {
        expect:
        Coordinate.parse('a:b') == Coordinate.parse('a:b')
        Coordinate.parse(':b') == Coordinate.parse(':b')

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

    def "compare by version after name"() {
        expect:
        Coordinate.parse('a:b:1') == Coordinate.parse('a:b:1')

        and:
        Coordinate.parse('a:b:1') < Coordinate.parse('a:b:2')

        and:
        Coordinate.parse('a:b:3') > Coordinate.parse('a:b:2')
    }

    def "compare by classifier after version"() {
        expect:
        Coordinate.parse('g:n:1:a') == Coordinate.parse('g:n:1:a')

        and:
        Coordinate.parse('g:n:1:a') < Coordinate.parse('g:n:1:b')

        and:
        Coordinate.parse('g:n:1:c') > Coordinate.parse('g:n:1:b')
    }

    def "compare by extension after classifier"() {
        expect:
        Coordinate.parse('g:n:1:e@a') == Coordinate.parse('g:n:1:e@a')

        and:
        Coordinate.parse('g:n:1:e@a') < Coordinate.parse('g:n:1:e@b')

        and:
        Coordinate.parse('g:n:1:e@d') > Coordinate.parse('g:n:1:e@c')
    }
    
}
