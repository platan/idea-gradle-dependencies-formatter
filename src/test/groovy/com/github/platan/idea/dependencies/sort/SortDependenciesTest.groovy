package com.github.platan.idea.dependencies.sort

import com.intellij.testFramework.LightCodeInsightTestCase

class SortDependenciesTest extends LightCodeInsightTestCase {

    @Override
    protected String getTestDataPath() {
        return 'src/test/resources/actions/sort/'
    }

    public void test__no_sorting() {
        configureByFile("no_sorting.gradle")
        perform()
        checkResultByFile("no_sorting_after.gradle")
    }

    private perform() {
        SortDependenciesAction action = new SortDependenciesAction()
        action.getHandler().invoke(getProject(), getEditor(), getFile())
    }

}