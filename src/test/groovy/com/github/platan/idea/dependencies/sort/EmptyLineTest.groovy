package com.github.platan.idea.dependencies.sort

import spock.lang.Specification
import spock.lang.Unroll

class EmptyLineTest extends Specification {

    @Unroll
    def "remove empty line"() {
        expect:
        StringUtil.removeEmptyLines(stringWithEmptyLine) == cleared

        where:
        stringWithEmptyLine | cleared
        '\n   \n'           | '\n'
        '\n   \n\n'         | '\n'
        '\n   \n \n'        | '\n'
        '    \n   \n'       | '    \n'
        '\n   \n    '       | '\n    '
        '\n\n'              | '\n'
        '   \n\n'           | '   \n'
        '\n\n   '           | '\n   '
        '\n\t\n'            | '\n'
        '\n\t\n\t'          | '\n\t'
    }

    @Unroll
    def "do not modify text without empty line"() {
        expect:
        StringUtil.removeEmptyLines(stringWithEmptyLine) == stringWithEmptyLine

        where:
        stringWithEmptyLine | _
        '\n'                | _
        ' \n'               | _
        '\n '               | _
        '\na\n'             | _
    }

}
