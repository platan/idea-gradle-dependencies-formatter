package com.github.platan.idea.dependencies.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.util.registry.Registry
import com.intellij.psi.impl.source.PostprocessReformattingAspect
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

class NotationIntentionTest extends LightCodeInsightFixtureTestCase {

    NotationIntentionTest() {
        Registry.get("ide.check.structural.psi.text.consistency.in.tests").setValue(false)
    }

    void wait_for_implementation__test_convert_to_map_and_to_string_again() {
        myFixture.configureByText("build.groovy", '''dependencies {
    compile 'com.google.<caret>guava:guava:18.0'
}''')
        runIntention('Convert to map notation')

        myFixture.checkResult('''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0'
}'''
        )

        runIntention('Convert to string notation')

        myFixture.checkResult('''dependencies {
    compile 'com.google.guava:guava:18.0'
}''')
    }

    void test_convert_to_string_and_to_map_again() {
        myFixture.configureByText("build.groovy", '''dependencies {
    compile group: 'co<caret>m.google.guava', name: 'guava', version: '18.0'
}''')

        runIntention('Convert to string notation')

        myFixture.checkResult('''dependencies {
    compile 'com.google.guava:guava:18.0'
}'''
        )

        runIntention('Convert to map notation')

        myFixture.checkResult('''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0'
}''')
    }

    protected void runIntention(String intention) {
        List<IntentionAction> list = myFixture.filterAvailableIntentions(intention)
        assert list.size() == 1, "An intention '$intention' should be applicable to: \n${myFixture.editor.document.text}\n"
        myFixture.launchAction(list.first())
        PostprocessReformattingAspect.getInstance(project).doPostponedFormatting()
    }
}
