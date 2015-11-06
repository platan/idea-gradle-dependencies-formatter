package com.github.platan.idea.dependencies.sort

import kotlin.text.Regex

object StringUtil {

    @JvmStatic
    fun removeEmptyLines(string: String): String {
        return string.replace(Regex("\n[ \t]*(?=\n)"), "")
    }

}