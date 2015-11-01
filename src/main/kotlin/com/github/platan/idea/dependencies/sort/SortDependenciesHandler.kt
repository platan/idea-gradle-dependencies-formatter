package com.github.platan.idea.dependencies.sort

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ArrayUtil
import com.intellij.util.containers.ContainerUtil
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall

class SortDependenciesHandler : CodeInsightActionHandler {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        object : SimpleWriteCommandAction(project, "Sort dependencies", file) {
            override fun run() {
                val factory = GroovyPsiElementFactory.getInstance(project)
                val closableBlocks = PsiTreeUtil.getChildrenOfTypeAsList(file, GrMethodCall::class.java)
                var dependenciesBlock: GrCall? = ContainerUtil.find(closableBlocks, object : Condition<GrMethodCall> {
                    override fun value(call: GrMethodCall): Boolean {
                        val expression = call.invokedExpression
                        return expression != null && "dependencies" == expression.text
                    }
                })
                if (dependenciesBlock == null) {
                    return
                }
                val closableBlock = ArrayUtil.getFirstElement(dependenciesBlock.closureArguments)
                if (closableBlock != null) {
                    var statements: List<GrApplicationStatement> = PsiTreeUtil.getChildrenOfTypeAsList(closableBlock,
                            GrApplicationStatement::class.java)
                    statements = statements.sortedBy { it.text }
                    for (statement in statements) {
                        statement.delete()
                    }
                    for (statement in statements) {
                        closableBlock.addStatementBefore(factory.createStatementFromText(statement.text), null)
                    }
                }
            }
        }.execute()
    }

    override fun startInWriteAction(): Boolean = false
}