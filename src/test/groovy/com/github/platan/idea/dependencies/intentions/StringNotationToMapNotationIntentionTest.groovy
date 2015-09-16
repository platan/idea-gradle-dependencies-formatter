package com.github.platan.idea.dependencies.intentions

class StringNotationToMapNotationIntentionTest extends IntentionTestBase {

    @Override
    protected String getBasePath() {
        '../src/test/resources/intentions/stringNotationToMapNotation/'
    }

    StringNotationToMapNotationIntentionTest() {
        super('Convert to map notation')
    }

    void testSimpleCase() {
        doTest()
    }

}
