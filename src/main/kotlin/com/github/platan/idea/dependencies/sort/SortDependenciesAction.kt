package com.github.platan.idea.dependencies.sort

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.plugins.gradle.util.GradleConstants

class SortDependenciesAction() : CodeInsightAction() {

    private val DOT_GRADLE = "." + GradleConstants.EXTENSION

    override fun getHandler(): CodeInsightActionHandler = SortDependenciesHandler()

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return super.isValidForFile(project, editor, file) && isGradleFile(file)
    }

    private fun isGradleFile(file: PsiFile): Boolean {
        return file.name.endsWith(DOT_GRADLE) && file.name != GradleConstants.SETTINGS_FILE_NAME
    }
}