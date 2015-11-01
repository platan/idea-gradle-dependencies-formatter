package com.github.platan.idea.dependencies.sort

import com.intellij.testFramework.LightCodeInsightTestCase

class SortDependenciesTest extends LightCodeInsightTestCase {

    @Override
    protected String getTestDataPath() {
        'src/test/resources/actions/sort/'
    }

    void test__already_sorted() {
        configureByFile("already_sorted.gradle")
        perform()
        checkResultByFile("already_sorted_after.gradle")
    }

    void test__simple() {
        configureByFile("simple.gradle")
        perform()
        checkResultByFile("simple_after.gradle")
    }

    private perform() {
        SortDependenciesAction action = new SortDependenciesAction()
        action.handler.invoke(project, editor, file)
    }

}
