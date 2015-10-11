package com.github.platan.idea.dependencies.intentions;

import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.DOUBLE_QUOTES;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.TRIPLE_DOUBLE_QUOTES;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.escapeAndUnescapeSymbols;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.getStartQuote;
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
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrString;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.arguments.GrArgumentListImpl;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapNotationToStringNotationIntention extends Intention {

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor) throws IncorrectOperationException {
        GrArgumentListImpl argumentList = (GrArgumentListImpl) element.getParent().getParent();
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

    @NotNull
    private Map<String, String> toMap(GrNamedArgument[] namedArguments) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (GrNamedArgument namedArgument : namedArguments) {
            String key = namedArgument.getLabel().getText();
            GrExpression expression = namedArgument.getExpression();
            String quote = getStartQuote(expression.getText());
            String value = removeQuotes(expression.getText());
            if (isInterpolableString(quote) && !isGstring(expression)) {
                String stringWithoutQuotes = removeQuotes(expression.getText());
                value = escapeAndUnescapeSymbols(stringWithoutQuotes, "", "\"$", new StringBuilder());
            }
            map.put(key, value);
        }
        return map;
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

    private boolean isGstring(PsiElement element) {
        return element instanceof GrLiteral && element instanceof GrString;
    }

    private boolean isInterpolableString(String quote) {
        return quote.equals(DOUBLE_QUOTES) || quote.equals(TRIPLE_DOUBLE_QUOTES);
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
