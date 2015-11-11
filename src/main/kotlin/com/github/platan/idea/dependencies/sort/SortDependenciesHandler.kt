package com.github.platan.idea.dependencies.sort

import com.github.platan.idea.dependencies.gradle.Coordinate
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil.getChildrenOfTypeAsList
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrCommandArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrString
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.DOUBLE_QUOTES
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.TRIPLE_DOUBLE_QUOTES
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.escapeAndUnescapeSymbols
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.getStartQuote
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.removeQuotes
import java.util.*

class SortDependenciesHandler : CodeInsightActionHandler {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        object : SimpleWriteCommandAction(project, "Sort dependencies", file) {
            override fun run() {
                val dependenciesClosure = findDependenciesClosure(file)
                if (dependenciesClosure != null) {
                    val factory = GroovyPsiElementFactory.getInstance(project)
                    sortDependencies(dependenciesClosure, factory)
                    removeEmptyLines(dependenciesClosure, factory)
                }
            }

            private fun findDependenciesClosure(psiFile: PsiFile): GrClosableBlock? {
                val methodCalls = getChildrenOfTypeAsList(psiFile, GrMethodCall::class.java)
                val dependenciesBlock = methodCalls.find { it.invokedExpression?.text == "dependencies" } ?: return null
                return dependenciesBlock.closureArguments.first()
            }

            private fun sortDependencies(dependenciesClosure: GrClosableBlock, factory: GroovyPsiElementFactory) {
                val statements = getChildrenOfTypeAsList(dependenciesClosure, GrApplicationStatement::class.java)
                statements.forEach { it.delete() }
                val byConfigurationName = compareBy<GrApplicationStatement> { it.firstChild.text }
                val byArgumentType = compareBy<GrApplicationStatement> { isDependencyNotation(it) }
                val byDependencyValue = compareBy<GrApplicationStatement> { getCoordinate(it) }
                statements.sortedWith(byConfigurationName.then(byArgumentType).then(byDependencyValue))
                        .forEach { dependenciesClosure.addStatementBefore(factory.createStatementFromText(it.text), null) }
            }

            private fun isDependencyNotation(it: GrApplicationStatement): Boolean {
                val argument = it.lastChild
                return argument is GrCommandArgumentList &&
                        (argument.firstChild is GrLiteral && Coordinate.isStringNotationCoordinate(argument.firstChild.text) ||
                                argument.firstChild is GrMethodCall && Coordinate.isStringNotationCoordinate(argument.firstChild.firstChild.text) ||
                                Coordinate.isValidMap(toMap(argument.namedArguments)))
            }

            private fun getCoordinate(it: GrApplicationStatement): Coordinate? {
                if (it.lastChild is GrCommandArgumentList && (it.lastChild.firstChild is GrLiteral
                        && Coordinate.isStringNotationCoordinate(it.lastChild.firstChild.text) )) {
                    return Coordinate.parse(removeQuotesAndUnescape(it.lastChild.firstChild))
                }
                if (it.lastChild.firstChild is GrMethodCall
                        && Coordinate.isStringNotationCoordinate(it.lastChild.firstChild.firstChild.text)) {
                    return Coordinate.parse(removeQuotesAndUnescape(it.lastChild.firstChild.firstChild))
                }
                if (it.lastChild is GrCommandArgumentList
                        && Coordinate.isValidMap(toMap((it.lastChild as GrCommandArgumentList).namedArguments))) {
                    return Coordinate.fromMap(toMap((it.lastChild as GrCommandArgumentList).namedArguments))
                }
                return null
            }

            private fun removeQuotesAndUnescape(expression: PsiElement): String {
                val quote = getStartQuote(expression.text)
                var value = removeQuotes(expression.text)
                if (isInterpolableString(quote) && !isGstring(expression)) {
                    val stringWithoutQuotes = removeQuotes(expression.text)
                    value = escapeAndUnescapeSymbols(stringWithoutQuotes, "", "\"$", StringBuilder())
                }
                return value
            }

            private fun isGstring(element: PsiElement): Boolean {
                return element is GrString
            }

            private fun isInterpolableString(quote: String): Boolean {
                return quote == DOUBLE_QUOTES || quote == TRIPLE_DOUBLE_QUOTES
            }

            private fun toMap(namedArguments: Array<GrNamedArgument>): Map<String, String> {
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

            private fun removeEmptyLines(dependenciesClosure: GrClosableBlock, factory: GroovyPsiElementFactory) {
                val dependenciesClosureText = dependenciesClosure.text
                val withoutEmptyLines = StringUtil.removeEmptyLines(dependenciesClosureText)
                if (withoutEmptyLines != dependenciesClosureText) {
                    dependenciesClosure.replace(factory.createClosureFromText(withoutEmptyLines))
                }
            }
        }.execute()
    }

    override fun startInWriteAction(): Boolean = false
}