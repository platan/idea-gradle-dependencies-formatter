package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrString;
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PsiElementCoordinate {
    private static final String NAME_KEY = "name";
    private static final String GROUP_KEY = "group";
    private static final String VERSION_KEY = "version";
    private static final String CLASSIFIER_KEY = "classifier";
    private static final String EXT_KEY = "ext";
    private final PsiElement group;
    private final PsiElement name;
    private final PsiElement version;
    private final PsiElement classifier;
    private final PsiElement extension;

    public PsiElementCoordinate(@Nullable PsiElement group, PsiElement name, @Nullable PsiElement version, @Nullable PsiElement classifier,
                                @Nullable PsiElement extension) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.classifier = classifier;
        this.extension = extension;
    }

    public String toGrStringNotation() {
        List<PsiElement> allElements = Stream.of(group, name, version, classifier, extension)
                .filter(java.util.Objects::nonNull).collect(Collectors.toList());
        boolean plainValues = allElements.stream().allMatch(this::isPlainValue);
        StringBuilder stringBuilder = new StringBuilder();
        if (group != null) {
            stringBuilder.append(getText(group, plainValues));
        }
        stringBuilder.append(':');
        stringBuilder.append(getText(name, plainValues));
        appendIfNotNull(stringBuilder, ':', version, plainValues);
        if (version == null && classifier != null) {
            stringBuilder.append(':');
        }
        appendIfNotNull(stringBuilder, ':', classifier, plainValues);
        appendIfNotNull(stringBuilder, '@', extension, plainValues);
        char quote = plainValues ? '\'' : '"';
        return String.format("%c%s%c", quote, stringBuilder.toString(), quote);
    }

    private boolean isPlainValue(PsiElement element) {
        return element instanceof GrLiteral && !(element instanceof GrString);
    }

    private String getText(PsiElement element, boolean plainValues) {
        if (element instanceof GrLiteral) {
            return GrStringUtil.removeQuotes(element.getText());
        }
        return element.getText();
    }

    private void appendIfNotNull(StringBuilder stringBuilder, char separator, PsiElement nullabeValue, boolean plainValues) {
        if (nullabeValue != null) {
            stringBuilder.append(separator);
            stringBuilder.append(getText(nullabeValue, plainValues));
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

    @Override
    public int hashCode() {
        return Objects.hashCode(group, name, version, classifier, extension);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PsiElementCoordinate other = (PsiElementCoordinate) obj;
        return Objects.equal(this.group, other.group)
                && Objects.equal(this.name, other.name)
                && Objects.equal(this.version, other.version)
                && Objects.equal(this.classifier, other.classifier)
                && Objects.equal(this.extension, other.extension);
    }

    @Override
    public String toString() {
        return "Coordinate{"
                + "group=" + group
                + ", name=" + name
                + ", version=" + version
                + ", classifier=" + classifier
                + ", extension=" + extension
                + '}';
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
            this.group = emptyToNull(group);
            return this;
        }

        private PsiElement emptyToNull(PsiElement value) {
            return value == null ? null : value;
        }

        public CoordinateBuilder withVersion(PsiElement version) {
            this.version = emptyToNull(version);
            return this;
        }

        public CoordinateBuilder withClassifier(PsiElement classifier) {
            this.classifier = emptyToNull(classifier);
            return this;
        }

        public CoordinateBuilder withExtension(PsiElement extension) {
            this.extension = emptyToNull(extension);
            return this;
        }

        public PsiElementCoordinate build() {
            return new PsiElementCoordinate(group, name, version, classifier, extension);
        }

    }

}
