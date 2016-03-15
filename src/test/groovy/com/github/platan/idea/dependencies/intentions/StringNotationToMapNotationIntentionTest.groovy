package com.github.platan.idea.dependencies.intentions

class StringNotationToMapNotationIntentionTest extends IntentionTestBase {

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
    compile ('com.google.<caret>guava:guava:18.0') {
        transitive = false
    }
}''',
                '''dependencies {
    compile (group: 'com.google.guava', name: 'guava', version: '18.0') {
        transitive = false
    }
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
}
