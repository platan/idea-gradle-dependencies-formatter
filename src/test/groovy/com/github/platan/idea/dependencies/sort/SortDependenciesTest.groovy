package com.github.platan.idea.dependencies.sort

import com.intellij.testFramework.LightCodeInsightTestCase

class SortDependenciesTest extends LightCodeInsightTestCase {

    @Override
    protected String getTestDataPath() {
        'src/test/resources/actions/sort/'
    }

    void test__no_sorting() {
        configureByFile("no_sorting.gradle")
        perform()
        checkResultByFile("no_sorting_after.gradle")
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
