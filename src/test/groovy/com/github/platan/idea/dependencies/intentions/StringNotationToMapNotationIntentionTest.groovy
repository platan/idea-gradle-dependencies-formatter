package com.github.platan.idea.dependencies.intentions

class StringNotationToMapNotationIntentionTest extends IntentionTestBase {
    private Random random = new Random()

    @Override
    protected String getTestDataPath() {
        this.getClass().getResource('/intentions/stringNotationToMapNotation/').path
    }

    StringNotationToMapNotationIntentionTest() {
        super('Convert to map notation')
    }

    void test_convert_string_notation_with_single_quote() {
        doTextTest('''dependencies {
    compile 'com.google.<caret>guava:guava:18.0'
}''',
                '''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0'
}''')
    }

    void test_convert_optional_dependency() {
        doTextTest('''dependencies {
    compile 'com.google.<caret>guava:guava:18.0', optional
}''',
                '''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0', optional
}''')
    }

    void test_convert_string_notation_with_ext() {
        doTextTest('''dependencies {
    compile 'com.google.<caret>guava:guava:18.0@jar'
}''',
                '''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0', ext: 'jar'
}''')
    }

    void test_convert_string_notation_with_double_quote() {
        doTextTest('''dependencies {
    compile "com.google.<caret>guava:guava:18.0"
}''',
                '''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0'
}''')
    }

    void test_convert_string_with_special_characters() {
        doTest()
    }

    void test_convert_string_notation_with_single_quote_and_brackets() {
        doTextTest('''dependencies {
    compile('com.google.<caret>guava:guava:18.0')
}''',
                '''dependencies {
    compile(group: 'com.google.guava', name: 'guava', version: '18.0')
}''')
    }

    void test_convert_string_notation_with_single_quote_and_brackets_and_closure() {
        doTextTest('''dependencies {
    compile('com.google.<caret>guava:guava:18.0') {
        transitive = false
    }
}''',
                '''dependencies {
    compile(group: 'com.google.guava', name: 'guava', version: '18.0') {
        transitive = false
    }
}''')
    }

    void test_convert_map_notation_and_map_notation() {
        doTextTest('''dependencies {
    <selection>testCompile 'junit:junit:4.1'
    testCompile group: 'junit', name: 'junit', version: '4.13'</selection>
}''',
                '''dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.1'
    testCompile group: 'junit', name: 'junit', version: '4.13'
}''')
    }

    void test_convert_interpolated_string() {
        doTest()
    }

    void test_do_not_find_intention() {
        doAntiTest('''dependencies {
    compile 'gu<caret>ava'
}''')
    }

    void test_do_not_find_intention_for_method_call_without_arguments() {
        doAntiTest('''dependencies {
    compile(<caret>)
}''')
    }

    void test_do_not_find_intention_for_variable_with_dependency() {
        doAntiTest('''dependencies {
    def guava = 'com.google.<caret>guava:guava:18.0'
}''')
    }

    void test_do_not_find_intention_for_string_with_errors() {
        doAntiTest('''dependencies {
    compile "com.google.<caret>guava:guava:$"
}''')
    }

    void test_do_not_find_intention_for_project_dependency() {
        doAntiTest('''dependencies {
    compile project(":l<caret>ib")
}''')
    }

    void test_do_not_find_intention_for_project_dependency_with_brackets() {
        doAntiTest('''dependencies {
    compile(project(":l<caret>ib"))
}''')
    }

    void test_do_not_find_intention_for_project_dependency_plus_closure() {
        doAntiTest('''dependencies {
    compile(project(":l<caret>ib")) {
    }
}''')
    }

    void test_do_not_find_intention_for_dependency_with_project_configuration() {
        doAntiTest('''dependencies {
    project("com.google.<caret>guava:guava:18.0'")
}''')
    }

    void test_convert_gstring_with_escaped_character() {
        doTest()
    }

    void test_convert_interpolated_triple_double_quoted_string() {
        doTest()
    }

    void test_convert_single_quoted_string() {
        doTest()
    }

    void test_convert_interpolated_string_dollar_brackets() {
        doTest()
    }

    void test_convert_dependency_caret_at_configuration() {
        doTextTest('''dependencies {
    com<caret>pile 'com.google.guava:guava:18.0'
}''',
                '''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0'
}''')
    }

    void test_convert_multiple_map_notation() {
        doTextTest('''dependencies {
    <selection><caret>compile('com.google.guava:guava:18.0') {
        transitive = false
    }
    testCompile 'junit:junit:4.13'</selection>
}''',
                '''dependencies {
    compile(group: 'com.google.guava', name: 'guava', version: '18.0') {
        transitive = false
    }
    testCompile group: 'junit', name: 'junit', version: '4.13'
}''')
    }

    void test_convert_selection_with_one_dependency() {
        doTextTest('''dependencies {
    <selection><caret>testCompile 'junit:junit:4.13'</selection>
}''',
                '''dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.13'
}''')
    }

    void test_convert_all_selected_elements() {
        doTextTest('''<selection><caret>
build {
    dependencies {
        compile 'com.google.guava:guava:18.0'
    }
}
dependencies {
    testCompile 'junit:junit:4.13'
}</selection>''',
                '''
build {
    dependencies {
        compile group: 'com.google.guava', name: 'guava', version: '18.0'
    }
}
dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.13'
}''')
    }

    void test_convert_selection_with_one_dependency_configuration_selected() {
        doTextTest('''dependencies {
    <selection><caret>t</selection>estCompile 'junit:junit:4.13'
}''',
                '''dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.13'
}''')
    }

    void test_do_not_find_intention_with_caret_in_build_gradle_without_dependencies() {
        def buildGradleContent = getClass().getResource('/intentions/build_no_dependencies.gradle').text
        for (int i = 0; i < buildGradleContent.length(); i++) {
            def content = new StringBuilder(buildGradleContent)
            content.insert(i, '<caret>')
            doAntiTest(content.toString())
        }
    }

    void test_do_not_find_intention_with_selection_in_build_gradle_without_dependencies() {
        def buildGradleContent = getClass().getResource('/intentions/build_no_dependencies.gradle').text
        def contentLength = buildGradleContent.length()
        def threshold = 10 * (1 / (contentLength * contentLength))
        for (int i = 0; i < contentLength; i++) {
            for (int j = i; j < contentLength; j++) {
                if (random.nextDouble() <= threshold) {
                    def content = new StringBuilder(buildGradleContent)
                    content.insert(j, '</selection>')
                    content.insert(i, '<selection><caret>')
                    doAntiTest(content.toString())
                }
            }
        }
    }
}
