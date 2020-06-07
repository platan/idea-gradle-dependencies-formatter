package com.github.platan.idea.dependencies.intentions;

import com.github.platan.idea.dependencies.gradle.Coordinate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.intentions.base.PsiElementPredicate;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrString;

import static com.github.platan.idea.dependencies.gradle.Coordinate.isStringNotationCoordinate;
import static org.jetbrains.plugins.groovy.lang.psi.util.ErrorUtil.containsError;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.DOUBLE_QUOTES;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.TRIPLE_DOUBLE_QUOTES;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.escapeAndUnescapeSymbols;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.getStartQuote;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.isStringLiteral;
import static org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil.removeQuotes;

public class StringNotationToMapNotationIntention extends SelectionIntention<GrMethodCall> {

    @Override
    protected void processIntention(@NotNull PsiElement element, @NotNull Project project, Editor editor) {
        GrMethodCall found = findElement(element, GrMethodCall.class);
        PsiElement firstArgument = getFirstArgument(found);
        if (firstArgument == null) {
            return;
        }

        String quote = getStartQuote(firstArgument.getText());
        String stringNotation = removeQuotes(firstArgument.getText());
        String mapNotation = Coordinate.parse(stringNotation).toMapNotation(quote);
        GrArgumentList argumentList = GroovyPsiElementFactory.getInstance(project).createArgumentListFromText(mapNotation);
        if (isInterpolableString(quote)) {
            replaceGStringMapValuesToString(argumentList, project);
        }
        for (GrNamedArgument namedArgument : argumentList.getNamedArguments()) {
            found.getArgumentList().addNamedArgument(namedArgument);
        }
        firstArgument.delete();
    }

    @Nullable
    private PsiElement getFirstArgument(@Nullable GrMethodCall element) {
        if (element == null) {
            return null;
        }
        if (element.getArgumentList().getAllArguments().length == 0) {
            return null;
        }
        return element.getArgumentList().getAllArguments()[0];
    }

    private boolean isInterpolableString(String quote) {
        return quote.equals(DOUBLE_QUOTES) || quote.equals(TRIPLE_DOUBLE_QUOTES);
    }

    private void replaceGStringMapValuesToString(GrArgumentList map, Project project) {
        for (PsiElement psiElement : map.getChildren()) {
            PsiElement lastChild = psiElement.getLastChild();
            if (lastChild instanceof GrLiteral && !(lastChild instanceof GrString)) {
                String stringWithoutQuotes = removeQuotes(lastChild.getText());
                String unescaped = escapeAndUnescapeSymbols(stringWithoutQuotes, "", "\"$", new StringBuilder());
                String string = String.format("'%s'", unescaped);
                lastChild.replace(GroovyPsiElementFactory.getInstance(project).createExpressionFromText(string));
            }
        }
    }

    @NotNull
    @Override
    protected PsiElementPredicate getElementPredicate() {
        return element -> {
            GrMethodCall found = findElement(element, GrMethodCall.class);
            PsiElement firstArgument = getFirstArgument(found);
            if (firstArgument == null) {
                return false;
            }

            return firstArgument instanceof GrLiteral
                    && !containsError(firstArgument)
                    && isStringLiteral((GrLiteral) firstArgument)
                    && isStringNotationCoordinate(removeQuotes(firstArgument.getText()));
        };
    }

    private <T> T findElement(PsiElement element, Class<T> aClass) {
        if (aClass.isInstance(element)) {
            return aClass.cast(element);
        }
        if (aClass.isInstance(element.getParent().getParent())) {
            return aClass.cast(element.getParent().getParent());
        }
        return null;
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

    @Override
    protected Class<GrMethodCall> elementTypeToFindInSelection() {
        return GrMethodCall.class;
    }
}
