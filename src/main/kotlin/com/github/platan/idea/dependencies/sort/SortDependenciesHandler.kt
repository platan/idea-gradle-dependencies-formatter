package com.github.platan.idea.dependencies.sort

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil.getChildrenOfTypeAsList
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

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
                    statements.sortedBy { it.text }
                            .forEach { closableBlock.addStatementBefore(factory.createStatementFromText(it.text), null) }
                }
            }
        }.execute()
    }

    override fun startInWriteAction(): Boolean = false
}