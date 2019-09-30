package com.github.platan.idea.dependencies.format

import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

class FormatDependenciesActionTest extends LightCodeInsightFixtureTestCase {

    void test__action_is_not_active_for_settings_gradle() {
        myFixture.configureByText("settings.gradle", """dependencies {
    compile 'junit:junit:4.11'
    compile 'com.google.guava:guava:17.0'
}""")
        CodeInsightAction action = new FormatDependenciesAction()

        assert !action.isValidForFile(project, myFixture.editor, myFixture.file)
    }

    void test__action_is_not_active_for_non_gradle_file() {
        myFixture.configureByText("test.groovy", """dependencies {
    compile 'junit:junit:4.11'
    compile 'com.google.guava:guava:17.0'
}""")
        CodeInsightAction action = new FormatDependenciesAction()

        assert !action.isValidForFile(project, myFixture.editor, myFixture.file)
    }

}
