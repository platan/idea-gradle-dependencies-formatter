package com.github.platan.idea.dependencies.sort

import com.intellij.psi.PsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrString
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil
import java.util.*

object DependencyUtil {

    @JvmStatic
    fun toMap(namedArguments: Array<GrNamedArgument>): Map<String, String> {
        val map = LinkedHashMap<String, String>()
        for (namedArgument in namedArguments) {
            val expression = namedArgument.expression
            if (namedArgument.label == null || expression == null) {
                continue
            }
            val key = namedArgument.label!!.text
            var value = removeQuotesAndUnescape(expression)
            map.put(key, value)
        }
        return map
    }

    fun removeQuotesAndUnescape(expression: PsiElement): String {
        val quote = GrStringUtil.getStartQuote(expression.text)
        var value = GrStringUtil.removeQuotes(expression.text)
        if (isInterpolableString(quote) && !isGstring(expression)) {
            val stringWithoutQuotes = GrStringUtil.removeQuotes(expression.text)
            value = GrStringUtil.escapeAndUnescapeSymbols(stringWithoutQuotes, "", "\"$", StringBuilder())
        }
        return value
    }

    @JvmStatic
    fun isGstring(element: PsiElement): Boolean {
        return element is GrString
    }

    @JvmStatic
    fun isInterpolableString(quote: String): Boolean {
        return quote == GrStringUtil.DOUBLE_QUOTES || quote == GrStringUtil.TRIPLE_DOUBLE_QUOTES
    }
}