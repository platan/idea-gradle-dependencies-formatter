package com.github.platan.idea.dependencies.intentions;

import com.github.platan.idea.dependencies.gradle.Coordinate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.intentions.base.Intention;
import org.jetbrains.plugins.groovy.intentions.base.PsiElementPredicate;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil;

public class StringNotationToMapNotationIntention extends Intention {

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor) throws IncorrectOperationException {
        if (!(element instanceof GrLiteral)) return;
        String stringNotation = GrStringUtil.removeQuotes(element.getText());
        String mapNotation = Coordinate.parse(stringNotation).toMapNotation();
        element.replace(GroovyPsiElementFactory.getInstance(project).createArgumentListFromText(mapNotation));
    }

    @NotNull
    @Override
    protected PsiElementPredicate getElementPredicate() {
        return new PsiElementPredicate() {
            @Override
            public boolean satisfiedBy(PsiElement element) {
                return element.getParent() instanceof GrArgumentList &&
                        element instanceof GrLiteral && GrStringUtil.isStringLiteral((GrLiteral) element) &&
                        Coordinate.isStringNotationCoordinate(GrStringUtil.removeQuotes(element.getText()));
            }
        };
    }

    @NotNull
    @Override
    public String getText() {
        return "Convert to map notation";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert string notation to map notation";
    }
}
