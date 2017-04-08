package com.github.platan.idea.dependencies.intentions;

import static com.github.platan.idea.dependencies.sort.DependencyUtil.isGstring;
import static com.github.platan.idea.dependencies.sort.DependencyUtil.isInterpolableString;
import static com.github.platan.idea.dependencies.sort.DependencyUtil.toMap;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.getStartQuote;

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
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.arguments.GrArgumentListImpl;

import java.util.Map;

public class MapNotationToStringNotationIntention extends Intention {

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor) throws IncorrectOperationException {
        GrArgumentList argumentList = (GrArgumentList) element.getParent().getParent();
        GrNamedArgument[] namedArguments = argumentList.getNamedArguments();
        String stringNotation = toStringNotation(namedArguments);
        for (GrNamedArgument namedArgument : namedArguments) {
            namedArgument.delete();
        }
        GrExpression expressionFromText = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(stringNotation);
        argumentList.add(expressionFromText);
    }

    private String toStringNotation(GrNamedArgument[] namedArguments) {
        Map<String, String> map = toMap(namedArguments);
        Coordinate coordinate = Coordinate.fromMap(map);
        boolean containsGstringValue = containsGstringValue(namedArguments);
        char quote = containsGstringValue ? '"' : '\'';
        return String.format("%c%s%c", quote, coordinate.toStringNotation(), quote);
    }

    private boolean containsGstringValue(GrNamedArgument[] namedArguments) {
        boolean containsGstringValue = false;
        for (GrNamedArgument namedArgument : namedArguments) {
            GrExpression expression = namedArgument.getExpression();
            String quote = getStartQuote(expression.getText());
            if (isInterpolableString(quote) && isGstring(expression)) {
                containsGstringValue = true;
                break;
            }
        }
        return containsGstringValue;
    }

    @NotNull
    @Override
    protected PsiElementPredicate getElementPredicate() {
        return new PsiElementPredicate() {
            @Override
            public boolean satisfiedBy(PsiElement element) {
                if (element == null || element.getParent() == null || !(element.getParent().getParent() instanceof GrArgumentListImpl)) {
                    return false;
                }
                GrArgumentListImpl parent = (GrArgumentListImpl) element.getParent().getParent();
                GrNamedArgument[] namedArguments = parent.getNamedArguments();
                if (namedArguments.length == 0) {
                    return false;
                }
                Map<String, String> map = toMap(namedArguments);
                return Coordinate.isValidMap(map);
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
