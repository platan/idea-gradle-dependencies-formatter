package com.github.platan.idea.dependencies.sort

import com.github.platan.idea.dependencies.gradle.Coordinate
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTreeUtil.getChildrenOfTypeAsList
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrCommandArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression
import java.util.*

class SortDependenciesHandler : CodeInsightActionHandler {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        WriteCommandAction.writeCommandAction(project, file).run<Exception> {
            val dependenciesClosures = findDependenciesClosures(file)
            val factory = GroovyPsiElementFactory.getInstance(project)
            dependenciesClosures.forEach { dependenciesClosure ->
                sortDependencies(dependenciesClosure, factory)
                removeEmptyLines(dependenciesClosure, factory)
            }
        }
    }

    private fun findDependenciesClosures(psiFile: PsiFile): List<GrClosableBlock> {
        return PsiTreeUtil.findChildrenOfType(psiFile, GrMethodCall::class.java)
                .filter { it.invokedExpression.text == "dependencies" }
                .map { it.closureArguments.first() }
    }

    private fun sortDependencies(dependenciesClosure: GrClosableBlock, factory: GroovyPsiElementFactory) {
        val statements = getChildrenOfTypeAsList(dependenciesClosure, GrMethodCall::class.java)
        statements.forEach { it.delete() }
        val byConfigurationName = compareBy<GrMethodCall> { it.firstChild.text }
        val byArgumentType = compareBy<GrMethodCall> { isCoordinate(it) }
        val byDependencyValue = compareBy<GrMethodCall> { getComparableValue(it) }
        statements.sortedWith(byConfigurationName.then(byArgumentType).then(byDependencyValue))
                .forEach { dependenciesClosure.addStatementBefore(factory.createStatementFromText(it.text), null) }
    }

    private fun isCoordinate(it: GrMethodCall): Boolean {
        return when (it) {
            is GrApplicationStatement -> {
                isCoordinate(it)
            }
            is GrMethodCallExpression -> {
                isCoordinate(it)
            }
            else -> false
        }
    }

    private fun isCoordinate(it: GrMethodCallExpression): Boolean {
        if (it.namedArguments.isNotEmpty()) {
            return Coordinate.isValidMap(DependencyUtil.toMap(it.namedArguments))
        }
        if (it.expressionArguments.isNotEmpty()) {
            return Coordinate.isStringNotationCoordinate(it.expressionArguments[0].text)
        }
        return false
    }

    private fun isCoordinate(it: GrApplicationStatement): Boolean {
        val argument = it.lastChild
        return (argument is GrCommandArgumentList &&
                (argument.firstChild is GrLiteral && Coordinate.isStringNotationCoordinate(argument.firstChild.text) ||
                        argument.firstChild is GrMethodCall && Coordinate.isStringNotationCoordinate(argument.firstChild.firstChild.text) ||
                        Coordinate.isValidMap(DependencyUtil.toMap(argument.namedArguments))))
    }

    private fun getComparableValue(it: GrMethodCall): Comparable<*>? {
        if (it is GrApplicationStatement) {
            val argument = it.lastChild
            if (argument is GrCommandArgumentList) {
                if (argument.firstChild is GrLiteral &&
                        Coordinate.isStringNotationCoordinate(argument.firstChild.text)) {
                    return Coordinate.parse(DependencyUtil.removeQuotesAndUnescape(argument.firstChild))
                }
                if (argument.firstChild is GrMethodCall &&
                        Coordinate.isStringNotationCoordinate(argument.firstChild.firstChild.text)) {
                    return Coordinate.parse(DependencyUtil.removeQuotesAndUnescape(argument.firstChild.firstChild))
                }
                if (argument.firstChild is GrMethodCallExpression) {
                    return argument.firstChild.text.toLowerCase(Locale.ENGLISH)
                }
                if (argument.firstChild is GrReferenceExpression) {
                    return argument.firstChild.text
                }
                if (Coordinate.isValidMap(DependencyUtil.toMap(argument.namedArguments))) {
                    return Coordinate.fromMap(DependencyUtil.toMap(argument.namedArguments))
                }
            }
        }
        if (it is GrMethodCallExpression) {
            if (it.namedArguments.isNotEmpty()) {
                return Coordinate.fromMap(DependencyUtil.toMap(it.namedArguments))
            }
            if (it.expressionArguments.isNotEmpty()) {
                return Coordinate.parse(DependencyUtil.removeQuotesAndUnescape(it.expressionArguments[0]))
            }
        }
        return null
    }

    private fun removeEmptyLines(dependenciesClosure: GrClosableBlock, factory: GroovyPsiElementFactory) {
        val dependenciesClosureText = dependenciesClosure.text
        val withoutEmptyLines = StringUtil.removeEmptyLines(dependenciesClosureText)
        if (withoutEmptyLines != dependenciesClosureText) {
            dependenciesClosure.replace(factory.createClosureFromText(withoutEmptyLines))
        }
    }

    override fun startInWriteAction(): Boolean = false
}