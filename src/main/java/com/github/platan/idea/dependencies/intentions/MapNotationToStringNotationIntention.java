package com.github.platan.idea.dependencies.intentions;

import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.removeQuotes;

import com.github.platan.idea.dependencies.gradle.Coordinate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.intentions.base.Intention;
import org.jetbrains.plugins.groovy.intentions.base.PsiElementPredicate;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.arguments.GrArgumentListImpl;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapNotationToStringNotationIntention extends Intention {

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor) throws IncorrectOperationException {
        GrNamedArgument[] namedArguments = ((GrArgumentListImpl) element.getParent().getParent()).getNamedArguments();
        Map<String, String> map = toMap(namedArguments);
        Coordinate coordinate = Coordinate.fromMap(map);
        String stringNotation = String.format("'%s'", coordinate.toStringNotation());
        element.getParent().getParent().replace(GroovyPsiElementFactory.getInstance(project).createExpressionFromText(stringNotation));
    }

    @NotNull
    private Map<String, String> toMap(GrNamedArgument[] namedArguments) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (GrNamedArgument namedArgument : namedArguments) {
            String key = namedArgument.getLabel().getText();
            String value = removeQuotes(namedArgument.getExpression().getText());
            map.put(key, value);
        }
        return map;
    }

    @NotNull
    @Override
    protected PsiElementPredicate getElementPredicate() {
        return new PsiElementPredicate() {
            @Override
            public boolean satisfiedBy(PsiElement element) {
                if (!(element.getParent().getParent() instanceof GrArgumentListImpl)) {
                    return false;
                }
                GrArgumentListImpl parent = (GrArgumentListImpl) element.getParent().getParent();
                GrNamedArgument[] namedArguments = parent.getNamedArguments();
                if (namedArguments.length == 0) {
                    return false;
                }
                return true;
            }
        };
    }

    @NotNull
    @Override
    public String getText() {
        return "Convert to string notation";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Convert map notation to string notation";
    }
}
