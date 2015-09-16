package com.github.platan.idea.dependencies.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.psi.impl.source.PostprocessReformattingAspect
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

abstract class IntentionTestBase extends LightCodeInsightFixtureTestCase {

    protected final String hint

    IntentionTestBase(String hint) {
        assert hint != null
        this.hint = hint
    }

    protected void doTest() {
        myFixture.configureByFile(getTestName(false) + ".gradle")
        List<IntentionAction> list = myFixture.filterAvailableIntentions(hint)
        myFixture.launchAction(assertOneElement(list))
        PostprocessReformattingAspect.getInstance(project).doPostponedFormatting()
        myFixture.checkResultByFile(getTestName(false) + "_after.gradle")
    }

}
