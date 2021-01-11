package com.github.platan.idea.dependencies.sort

import com.github.platan.idea.dependencies.gradle.GradleFileUtil.isGradleFile
import com.github.platan.idea.dependencies.gradle.GradleFileUtil.isSettingGradle
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class SortDependenciesAction : CodeInsightAction() {

    override fun getHandler(): CodeInsightActionHandler = SortDependenciesHandler()

    override fun isValidForFile(project: Project, editor: Editor, file: PsiFile): Boolean {
        return super.isValidForFile(project, editor, file) && isGradleFile(file) && !isSettingGradle(file)
    }

}