package com.github.platan.idea.dependencies.intentions;

import com.github.platan.idea.dependencies.gradle.Coordinate;
import com.github.platan.idea.dependencies.gradle.PsiElementCoordinate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.intentions.base.PsiElementPredicate;
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElementFactory;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrNamedArgument;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall;

import java.util.Map;

import static com.github.platan.idea.dependencies.sort.DependencyUtil.toMapWithPsiElementValues;
import static com.github.platan.idea.dependencies.sort.DependencyUtil.toSimpleMap;

public class MapNotationToStringNotationIntention extends SelectionIntention<GrMethodCall> {

    @Override
    protected void processIntention(@NotNull PsiElement element, @NotNull Project project, Editor editor) {
        GrArgumentList argumentList = getGrArgumentList(element);
        if (argumentList == null) {
            return;
        }
        toStringNotation(project, argumentList);
    }

    private void toStringNotation(@NotNull Project project, GrArgumentList argumentList) {
        GrNamedArgument[] namedArguments = argumentList.getNamedArguments();
        Map<String, PsiElement> map = toMapWithPsiElementValues(namedArguments);
        PsiElementCoordinate coordinate = PsiElementCoordinate.fromMap(map);
        String stringNotation = coordinate.toGrStringNotation();
        for (GrNamedArgument namedArgument : namedArguments) {
            namedArgument.delete();
        }
        GrExpression expressionFromText = GroovyPsiElementFactory.getInstance(project).createExpressionFromText(stringNotation);
        argumentList.add(expressionFromText);
    }

    @Override
    protected Class<GrMethodCall> elementTypeToFindInSelection() {
        return GrMethodCall.class;
    }

    @NotNull
    @Override
    protected PsiElementPredicate getElementPredicate() {
        return element -> {
            GrArgumentList argumentList = getGrArgumentList(element);
            if (argumentList == null) {
                return false;
            }
            GrNamedArgument[] namedArguments = argumentList.getNamedArguments();
            if (namedArguments.length == 0) {
                return false;
            }
            Map<String, String> map = toSimpleMap(namedArguments);
            // TODO pass keys only
            return Coordinate.isValidMap(map);
        };
    }

    private GrArgumentList getGrArgumentList(@NotNull PsiElement element) {
        if (element.getParent() == null || element.getParent().getParent() == null) {
            return null;
        } else if (element instanceof GrMethodCall) {
            return ((GrMethodCall) element).getArgumentList();
        } else if (element.getParent().getParent() instanceof GrMethodCall) {
            return ((GrMethodCall) element.getParent().getParent()).getArgumentList();
        } else if (element.getParent().getParent().getParent() instanceof GrMethodCall) {
            return ((GrMethodCall) element.getParent().getParent().getParent()).getArgumentList();
        }
        return null;
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
