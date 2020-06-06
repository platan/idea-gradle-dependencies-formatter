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

    void test_convert_multiple_map_notation() {
        doTextTest('''dependencies {
    <selection><caret>compile (group: 'com.google.guava', name: 'guava', version: '18.0') {
        transitive = false
    }
    testCompile group: 'junit', name: 'junit', version: '4.13'</selection>
}''',
                '''dependencies {
    compile ('com.google.guava:guava:18.0') {
        transitive = false
    }
    testCompile 'junit:junit:4.13'
}''')
    }

    void test_convert_from_selection_only() {
        doTextTest('''dependencies {
    <selection><caret>compile group: 'com.google.guava', name: 'guava', version: '18.0'
    testCompile group: 'junit', name: 'junit', version: '4.13'</selection>
    testCompile group: 'org.spockframework', name: 'spock-core', version: '1.3-groovy-2.5'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
    testCompile 'junit:junit:4.13'
    testCompile group: 'org.spockframework', name: 'spock-core', version: '1.3-groovy-2.5'
}''')
    }

    void test_convert_partially_selected_elements() {
        doTextTest('''dependencies {
    com<selection><caret>pile group: 'com.google.guava', name: 'guava', version: '18.0'
    testCompile group: 'junit', name: 'junit', </selection>version: '4.13'
    testCompile group: 'org.spockframework', name: 'spock-core', version: '1.3-groovy-2.5'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
    testCompile 'junit:junit:4.13'
    testCompile group: 'org.spockframework', name: 'spock-core', version: '1.3-groovy-2.5'
}''')
    }

    void test_convert_map_notation_and_string_notation() {
        doTextTest('''dependencies {
    <selection><caret>testCompile 'junit:junit:4.12'
    testCompile group: 'junit', name: 'junit', version: '4.13'</selection>
}''',
                '''dependencies {
    testCompile 'junit:junit:4.12'
    testCompile 'junit:junit:4.13'
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

    void test_convert_dependency_caret_at_configuration() {
        doTextTest('''dependencies {
    com<caret>pile group: 'com.google.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
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

    void test_convert_map_notation_with_variable_as_a_version() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: guavaVersion
}''',
                '''dependencies {
    compile "com.google.guava:guava:$guavaVersion"
}''')
    }

    void test_convert_map_notation_with_instance_property_as_a_version() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: versions.guava
}''',
                '''dependencies {
    compile "com.google.guava:guava:${versions.guava}"
}''')
    }

    void test_convert_map_notation_with_method_call_as_a_version() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: guavaVersion()
}''',
                '''dependencies {
    compile "com.google.guava:guava:${guavaVersion()}"
}''')
    }

    void test_convert_map_notation_with_primitive_value_as_a_version() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: 19.0
}''',
                '''dependencies {
    compile 'com.google.guava:guava:19.0'
}''')
    }

    void test_convert_map_notation_with_static_method_call_as_a_version() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: String.valueOf(19.0)
}''',
                '''dependencies {
    compile "com.google.guava:guava:${String.valueOf(19.0)}"
}''')
    }

    void test_convert_map_notation_with_concatenated_strings() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: '19' + '.' + '0'
}''',
                '''dependencies {
    compile "com.google.guava:guava:${'19' + '.' + '0'}"
}''')
    }

    void test_convert_map_notation_with_simple_slashy_string() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: /19.0/
}''',
                '''dependencies {
    compile 'com.google.guava:guava:19.0'
}''')
    }

    void test_convert_map_notation_with_interpolated_slashy_string() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: /${19.0}/
}''',
                '''dependencies {
    compile "com.google.guava:guava:${19.0}"
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

    void test_convert_map_notation_with_caret_before_configuration() {
        doTextTest('''dependencies {
    <caret>compile group: 'com.google.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
}''')
    }

    void test_convert_map_notation_with_caret_after_configuration() {
        doTextTest('''dependencies {
    compile<caret> group: 'com.google.guava', name: 'guava', version: '18.0'
}''',
                '''dependencies {
    compile 'com.google.guava:guava:18.0'
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
