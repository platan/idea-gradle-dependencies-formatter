package com.github.platan.idea.dependencies.intentions;

import com.github.platan.idea.dependencies.gradle.Coordinate;
import com.github.platan.idea.dependencies.gradle.PsiElementCoordinate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.intentions.base.Intention;
import org.jetbrains.plugins.groovy.intentions.base.PsiElementPredicate;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.arguments.GrArgumentListImpl;

import java.util.Map;

import static com.github.platan.idea.dependencies.sort.DependencyUtil.toMapWithPsiElementValues;
import static com.github.platan.idea.dependencies.sort.DependencyUtil.toSimpleMap;

public class MapNotationToStringNotationIntention extends Intention {

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor) {
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
        Map<String, PsiElement> map = toMapWithPsiElementValues(namedArguments);
        PsiElementCoordinate coordinate = PsiElementCoordinate.fromMap(map);
        return coordinate.toGrStringNotation();
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
                Map<String, String> map = toSimpleMap(namedArguments);
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
