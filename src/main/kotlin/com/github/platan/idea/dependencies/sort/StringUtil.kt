package com.github.platan.idea.dependencies.sort

import kotlin.text.Regex

object StringUtil {

    @JvmStatic
    fun containsEmptyLine(string: String): Boolean {
        return string.contains(Regex("\n[ ]*\n"))
    }

    @JvmStatic
    fun removeEmptyLines(string: String): String {
        return string.replace(Regex("\n[ ]*(?=\n)"), "")
    }

}