package com.github.platan.idea.dependencies.intentions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.intentions.base.Intention;

import java.util.Collection;
import java.util.Collections;

abstract public class SelectionIntention<T extends PsiElement> extends Intention {

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (isAvailableForSelection(project, editor, file)) {
            getElements(editor, file, elementTypeToFindInSelection()).forEach(element ->
                    processIntention(element, project, editor));
        } else if (super.isAvailable(project, editor, file)) {
            super.invoke(project, editor, file);
        }
    }

    abstract protected Class<T> elementTypeToFindInSelection();

    protected boolean isAvailableForSelection(Project project, Editor editor, PsiFile file) {
        Collection<T> children = getElements(editor, file, elementTypeToFindInSelection());
        return children.stream().anyMatch(element -> getElementPredicate().satisfiedBy(element));
    }

    @NotNull
    private Collection<T> getElements(Editor editor, PsiFile file, Class<T> type) {
        SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
            int startOffset = selectionModel.getSelectionStart();
            int endOffset = selectionModel.getSelectionEnd();
            PsiElement startingElement = file.getViewProvider().findElementAt(startOffset, file.getLanguage());
            PsiElement endingElement = file.getViewProvider().findElementAt(endOffset - 1, file.getLanguage());
            if (startingElement != null && endingElement != null) {
                if (startingElement == endingElement) {
                    // allow org.jetbrains.plugins.groovy.intentions.base.Intention#isAvailable to handle this case
                    return Collections.emptySet();
                }
                PsiElement commonParent = PsiTreeUtil.findCommonParent(startingElement, endingElement);
                if (commonParent != null) {
                    return PsiTreeUtil.findChildrenOfType(commonParent, type);
                }
            }
        }
        return Collections.emptySet();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return isAvailableForSelection(project, editor, file) || super.isAvailable(project, editor, file);
    }
}
