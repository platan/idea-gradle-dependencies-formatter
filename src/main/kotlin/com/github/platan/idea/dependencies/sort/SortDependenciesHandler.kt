package com.github.platan.idea.dependencies.sort

import com.github.platan.idea.dependencies.gradle.Coordinate
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil.getChildrenOfTypeAsList
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrCommandArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral

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
                val byArgumentType = compareBy<GrApplicationStatement> { isCoordinate(it) }
                val byDependencyValue = compareBy<GrApplicationStatement> { getComparableValue(it) }
                statements.sortedWith(byConfigurationName.then(byArgumentType).then(byDependencyValue))
                        .forEach { dependenciesClosure.addStatementBefore(factory.createStatementFromText(it.text), null) }
            }

            private fun isCoordinate(it: GrApplicationStatement): Boolean {
                val argument = it.lastChild
                return argument is GrCommandArgumentList &&
                        (argument.firstChild is GrLiteral && Coordinate.isStringNotationCoordinate(argument.firstChild.text) ||
                                argument.firstChild is GrMethodCall && Coordinate.isStringNotationCoordinate(argument.firstChild.firstChild.text) ||
                                Coordinate.isValidMap(DependencyUtil.toMap(argument.namedArguments)))
            }

            private fun getComparableValue(it: GrApplicationStatement): Comparable<*>? {
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
                    if (argument.firstChild is GrReferenceExpression) {
                        return argument.firstChild.text;
                    }
                    if (Coordinate.isValidMap(DependencyUtil.toMap(argument.namedArguments))) {
                        return Coordinate.fromMap(DependencyUtil.toMap(argument.namedArguments))
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
        }.execute()
    }

    override fun startInWriteAction(): Boolean = false
}