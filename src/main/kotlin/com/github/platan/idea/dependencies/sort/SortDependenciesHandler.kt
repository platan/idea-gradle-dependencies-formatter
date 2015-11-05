package com.github.platan.idea.dependencies.sort

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil.getChildrenOfTypeAsList
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.removeQuotes

class SortDependenciesHandler : CodeInsightActionHandler {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        object : SimpleWriteCommandAction(project, "Sort dependencies", file) {
            override fun run() {
                val methodCalls = getChildrenOfTypeAsList(file, GrMethodCall::class.java)
                val dependenciesBlock = methodCalls.find { it.invokedExpression?.text == "dependencies" } ?: return
                val closableBlock = dependenciesBlock.closureArguments.first()
                if (closableBlock != null) {
                    val statements = getChildrenOfTypeAsList(closableBlock, GrApplicationStatement::class.java)
                    statements.forEach { it.delete() }
                    val factory = GroovyPsiElementFactory.getInstance(project)
                    val byConfigurationName = compareBy<GrApplicationStatement> ({ it.firstChild.text })
                    val byArgumentType = compareBy<GrApplicationStatement> ({ it.lastChild?.firstChild is GrLiteral })
                    val byDependencyValue = compareBy<GrApplicationStatement> ({ removeQuotes(it.lastChild.text) })
                    statements.sortedWith (byConfigurationName.then(byArgumentType).then(byDependencyValue))
                            .forEach { closableBlock.addStatementBefore(factory.createStatementFromText(it.text), null) }
                    removeEmptyLines(closableBlock, factory)
                }
            }

            private fun removeEmptyLines(closableBlock: GrClosableBlock, factory: GroovyPsiElementFactory) {
                if (StringUtil.containsEmptyLine(closableBlock.text)) {
                    val noEmptyLines = StringUtil.removeEmptyLines(closableBlock.text)
                    closableBlock.replace(factory.createClosureFromText(noEmptyLines))
                }
            }
        }.execute()
    }

    override fun startInWriteAction(): Boolean = false
}