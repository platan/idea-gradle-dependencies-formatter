package com.github.platan.idea.dependencies.intentions

class StringNotationToMapNotationIntentionTest extends IntentionTestBase {

    StringNotationToMapNotationIntentionTest() {
        super('Convert to map notation')
    }

    void test_convert() {
        doTextTest("""dependencies {
    compile 'com.google.<caret>guava:guava:18.0'
}""",
                """dependencies {
    compile group: 'com.google.guava', name: 'guava', version: '18.0'
}""")
    }

    void test_do_not_find_intention() {
        doAntiTest("""dependencies {
    compile 'gu<caret>ava'
}""")
    }

}
