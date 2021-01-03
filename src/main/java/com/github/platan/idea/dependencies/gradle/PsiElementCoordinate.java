package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Preconditions;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GrReferenceElement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrString;
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PsiElementCoordinate extends BaseCoordinate<PsiElement> {

    public PsiElementCoordinate(@Nullable PsiElement group, PsiElement name, @Nullable PsiElement version,
                                @Nullable PsiElement classifier, @Nullable PsiElement extension) {
        super(group, name, version, classifier, extension);
    }

    public String toGrStringNotation() {
        StringBuilder stringBuilder = new StringBuilder();
        if (group != null) {
            stringBuilder.append(getText(group));
        }
        stringBuilder.append(':');
        stringBuilder.append(getText(name));
        appendIfNotNull(stringBuilder, ':', version);
        if (version == null && classifier != null) {
            stringBuilder.append(':');
        }
        appendIfNotNull(stringBuilder, ':', classifier);
        appendIfNotNull(stringBuilder, '@', extension);
        List<PsiElement> allElements = Stream.of(group, name, version, classifier, extension)
                .filter(java.util.Objects::nonNull).collect(Collectors.toList());
        boolean plainValues = allElements.stream().allMatch(this::isPlainValue);
        char quote = plainValues ? '\'' : '"';
        return String.format("%c%s%c", quote, stringBuilder.toString(), quote);
    }

    private boolean isPlainValue(PsiElement element) {
        return element instanceof GrLiteral && !(element instanceof GrString);
    }

    private String getText(PsiElement element) {
        if (element instanceof GrLiteral) {
            GrLiteral grLiteral = (GrLiteral) element;
            if (grLiteral.getValue() != null) {
                return grLiteral.getValue().toString();
            }
            return GrStringUtil.removeQuotes(element.getText());
        }
        if (element instanceof GrReferenceElement) {
            GrReferenceElement referenceElement = (GrReferenceElement) element;
            if (referenceElement.getQualifiedReferenceName().equals(referenceElement.getReferenceName())) {
                return String.format("$%s", element.getText());
            } else {
                return String.format("${%s}", element.getText());
            }
        }
        return String.format("${%s}", element.getText());
    }

    private void appendIfNotNull(StringBuilder stringBuilder, char separator, PsiElement nullableValue) {
        if (nullableValue != null) {
            stringBuilder.append(separator);
            stringBuilder.append(getText(nullableValue));
        }
    }

    public static PsiElementCoordinate fromMap(Map<String, PsiElement> map) {
        PsiElement name = map.get(NAME_KEY);
        Preconditions.checkArgument(java.util.Objects.nonNull(name), "'name' element is required. ");
        CoordinateBuilder coordinateBuilder = CoordinateBuilder.aCoordinate(name);
        if (java.util.Objects.nonNull(map.get(GROUP_KEY))) {
            coordinateBuilder.withGroup(map.get(GROUP_KEY));
        }
        if (java.util.Objects.nonNull(map.get(VERSION_KEY))) {
            coordinateBuilder.withVersion(map.get(VERSION_KEY));
        }
        if (java.util.Objects.nonNull(map.get(CLASSIFIER_KEY))) {
            coordinateBuilder.withClassifier(map.get(CLASSIFIER_KEY));
        }
        if (java.util.Objects.nonNull(map.get(EXT_KEY))) {
            coordinateBuilder.withExtension(map.get(EXT_KEY));
        }
        return coordinateBuilder.build();
    }

    public static class CoordinateBuilder {
        private PsiElement group = null;
        private PsiElement name;
        private PsiElement version = null;
        private PsiElement classifier = null;
        private PsiElement extension = null;

        private CoordinateBuilder() {
        }

        public static CoordinateBuilder aCoordinate(PsiElement name) {
            CoordinateBuilder coordinateBuilder = new CoordinateBuilder();
            coordinateBuilder.name = name;
            return coordinateBuilder;
        }

        public CoordinateBuilder withGroup(PsiElement group) {
            this.group = group;
            return this;
        }

        public CoordinateBuilder withVersion(PsiElement version) {
            this.version = version;
            return this;
        }

        public CoordinateBuilder withClassifier(PsiElement classifier) {
            this.classifier = classifier;
            return this;
        }

        public CoordinateBuilder withExtension(PsiElement extension) {
            this.extension = extension;
            return this;
        }

        public PsiElementCoordinate build() {
            return new PsiElementCoordinate(group, name, version, classifier, extension);
        }

    }

}
