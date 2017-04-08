package com.github.platan.idea.dependencies.intentions

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.intentions.base.PsiElementPredicate
import spock.lang.Specification
import spock.lang.Subject

class MapNotationToStringNotationIntentionUnitTest extends Specification {

    @Subject
    private PsiElementPredicate predicate = new MapNotationToStringNotationIntention().elementPredicate

    def "predicate should handle element = null"() {
        expect:
        !predicate.satisfiedBy(null)
    }

    def "predicate should handle element with getParent() = null"() {
        given:
        def element = Stub(PsiElement) {
            getParent() >> null
        }

        expect:
        !predicate.satisfiedBy(element)
    }

    def "predicate should handle element with getParent().getParent() = null"() {
        given:
        def element = Stub(PsiElement) {
            getParent() >> Stub(PsiElement) {
                getParent() >> null
            }
        }

        expect:
        !predicate.satisfiedBy(element)
    }
}
