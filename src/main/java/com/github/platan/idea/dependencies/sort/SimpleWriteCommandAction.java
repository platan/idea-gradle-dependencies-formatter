package com.github.platan.idea.dependencies.sort;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public abstract class SimpleWriteCommandAction extends WriteCommandAction.Simple {
    protected SimpleWriteCommandAction(Project project, PsiFile... files) {
        super(project, files);
    }

    protected SimpleWriteCommandAction(Project project, String commandName, PsiFile... files) {
        super(project, commandName, files);
    }

    protected SimpleWriteCommandAction(Project project, String name, String groupID, PsiFile... files) {
        super(project, name, groupID, files);
    }
}
