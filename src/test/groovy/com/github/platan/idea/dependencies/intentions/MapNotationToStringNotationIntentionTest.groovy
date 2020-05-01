package com.github.platan.idea.dependencies.intentions

class MapNotationToStringNotationIntentionTest extends IntentionTestBase {

    @Override
    protected String getTestDataPath() {
        this.getClass().getResource('/intentions/mapNotationToStringNotation/').path
    }

    MapNotationToStringNotationIntentionTest() {
        super('Convert to string notation')
    }

    void test_convert_map_notation_without_version() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava'
}''',
                '''dependencies {
    compile 'com.google.guava:guava'
}''')
    }

    void test_convert_optional_dependency() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: '18.0', optional
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0', optional
}''')
    }

    void test_convert_map_notation_with_classifier() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: '18.0', classifier: 'sources'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0:sources'
}''')
    }

    void ignore_test_convert_map_notation_with_variable_as_a_version() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: guavaVersion
}''',
                '''dependencies {
    compile "com.google.guava:guava:$guavaVersion"
}''')
    }

    void test_convert_map_notation_with_classifier_and_ext() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: '18.0', classifier: 'sources', ext: 'jar'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0:sources@jar'
}''')
    }

    void test_convert_map_notation_with_ext() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: '18.0', ext: 'jar'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0@jar'
}''')
    }

    void test_convert_map_notation_with_caret_after_group_semicolon() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
}''')
    }

    void test_convert_map_notation_with_caret_before_group_semicolon() {
        doTextTest('''dependencies {
    compile group<caret>: 'com.google.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
}''')
    }

    void test_convert_map_notation_with_caret_on_group() {
        doTextTest('''dependencies {
    compile gro<caret>up: 'com.google.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
}''')
    }

    void test_convert_map_notation_with_caret_on_group_value() {
        doTextTest('''dependencies {
    compile group: 'com.go<caret>ogle.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
}''')
    }

    void test_convert_map_notation_with_caret_before_group() {
        doTextTest('''dependencies {
    compile <caret>group: 'com.google.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
}''')
    }

    void test_intention_not_applicable_to_map_notation_and_caret_after_configuration() {
        doAntiTest('''dependencies {
    compile<caret> group: 'com.google.guava', name: 'guava', version: '18.0'
}''')
    }

    void test_do_not_find_intention_for_single_argument() {
        doAntiTest('''dependencies {
    compile 'gu<caret>ava'
}''')
    }

    void test_do_not_find_intention_for_map_notation_without_required_elements() {
        doAntiTest('''dependencies {
    compile name: '<caret>guava', version: '18.0'
}''')
    }

    void test_do_not_find_intention_for_map_notation_with_unknown_property() {
        doAntiTest('''dependencies {
    compile <caret>group: 'com.google.guava', name: 'guava', version: '18.0', unknownProperty: 'cat'
}''')
    }

    void test_convert_interpolated_string() {
        doTest()
    }

    void test_convert_string_with_special_characters() {
        doTest()
    }

    void test_convert_interpolated_string_dollar_brackets() {
        doTest()
    }

    void test_convert_gstring_with_special_characters() {
        doTest()
    }

    void test_convert_interpolated_triple_quote_string() {
        doTest()
    }

    void test_convert_dependency_with_closure() {
        doTest()
    }

}
