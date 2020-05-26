package com.github.platan.idea.dependencies.sort

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase

class SortDependenciesTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        this.getClass().getResource('/actions/sort/').path
    }

    void test__already_sorted() {
        doTest()
    }

    void test__simple() {
        doTest()
    }

    void test__ignore_quotation_mark() {
        doTest()
    }

    void test__by_configuration_name() {
        doTest()
    }

    void test__with_comments() {
        doTest()
    }

    void test__remove_empty_lines() {
        doTest()
    }

    void test__with_closure() {
        doTest()
    }

    void test__with_variable() {
        doTest()
    }

    void test__with_method() {
        doTest()
    }

    void test__map_notation() {
        doTest()
    }

    void test__with_quotation_mark() {
        doTest()
    }

    void test__with_escaped_characters() {
        doTest()
    }

    void test__dependency_list() {
        doTest()
    }

    void test__variables() {
        doTest()
    }

    void test__method_call() {
        doTest()
    }

    void test__empty_dependencies_block() {
        doTest()
    }

    void test__empty_file() {
        doTest()
    }

    void test__invalid() {
        doTest()
    }

    void test__build_dependencies_simple() {
        doTest()
    }

    private doTest() {
        def fileName = getTestName(false).replaceFirst('__', '')
        myFixture.configureByFile("${fileName}.groovy")
        SortDependenciesAction action = new SortDependenciesAction()
        action.handler.invoke(project, editor, file)
        myFixture.checkResultByFile("${fileName}_after.groovy")
    }

}
