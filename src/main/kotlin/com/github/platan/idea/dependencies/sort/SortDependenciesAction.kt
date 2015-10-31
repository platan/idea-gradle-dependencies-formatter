package com.github.platan.idea.dependencies.sort

import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction

class SortDependenciesAction() : CodeInsightAction() {
    override fun getHandler(): CodeInsightActionHandler = SortDependenciesHandler()
}