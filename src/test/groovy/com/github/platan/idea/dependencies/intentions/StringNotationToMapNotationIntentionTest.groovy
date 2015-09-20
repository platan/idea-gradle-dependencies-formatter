package com.github.platan.idea.dependencies.intentions

class StringNotationToMapNotationIntentionTest extends IntentionTestBase {

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

    void test_convert_string_notation_with_double_quote() {
        doTextTest('''dependencies {
    compile "com.google.<caret>guava:guava:18.0"
}''',
                '''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0'
}''')
    }

    void test_convert_string_with_escaped_characters() {
        doTextTest('''dependencies {
    compile 'com.google.<caret>guava:guava:$guavaVersion'
}''',
                '''dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '$guavaVersion'
}''')
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

}
