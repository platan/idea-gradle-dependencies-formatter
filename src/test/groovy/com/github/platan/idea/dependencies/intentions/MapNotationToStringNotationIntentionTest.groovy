package com.github.platan.idea.dependencies.intentions

class MapNotationToStringNotationIntentionTest extends IntentionTestBase {
    @Override
    protected String getBasePath() {
        '../src/test/resources/intentions/mapNotationToStringNotation/'
    }

    MapNotationToStringNotationIntentionTest() {
        super('Convert to string notation')
    }

    void test_convert_map_notation_with_single_quote() {
        doTextTest('''dependencies {
    compile group:<caret> 'com.google.guava', name: 'guava', version: '18.0'
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

}
