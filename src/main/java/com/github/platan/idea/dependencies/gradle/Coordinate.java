package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

public class Coordinate {
    private static final Splitter ON_SEMICOLON_SPLITTER = Splitter.on(":").limit(4);
    private static final Splitter ON_AMP_SPLITTER = Splitter.on("@").limit(2);
    private final Optional<String> group;
    private final String name;
    private final Optional<String> version;
    private final Optional<String> classifier;
    private final Optional<String> extension;

    public Coordinate(Optional<String> group, String name, Optional<String> version, Optional<String> classifier, Optional<String>
            extension) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.classifier = classifier;
        this.extension = extension;
    }

    public static Coordinate parse(String stringNotation) {
        Preconditions.checkArgument(!stringNotation.trim().isEmpty(), "Coordinate is empty!");
        List<String> parts = Lists.newArrayList(ON_SEMICOLON_SPLITTER.split(stringNotation));
        int numberOfParts = parts.size();
        CoordinateBuilder coordinateBuilder = null;
        if (numberOfParts == 1) {
            coordinateBuilder = CoordinateBuilder.aCoordinate(getAt(parts, 0));
        } else if (numberOfParts == 2) {
            coordinateBuilder = CoordinateBuilder.aCoordinate(getAt(parts, 1)).withGroup(getAt(parts, 0));
        } else if (numberOfParts >= 3) {
            coordinateBuilder = CoordinateBuilder.aCoordinate(getAt(parts, 1)).withGroup(getAt(parts, 0)).withVersion(getAt(parts, 2));
        }
        if (numberOfParts == 4) {
            List<String> classifierPlusExtension = Lists.newArrayList(ON_AMP_SPLITTER.split(getAt(parts, 3)));
            if (classifierPlusExtension.size() >= 1) {
                coordinateBuilder.withClassifier(getAt(classifierPlusExtension, 0));
            }
            if (classifierPlusExtension.size() == 2) {
                coordinateBuilder.withExtension(getAt(classifierPlusExtension, 1));
            }
        }
        return coordinateBuilder.build();
    }

    private static String getAt(List<String> list, int index) {
        String element = list.get(index);
        Preconditions.checkArgument(!element.trim().isEmpty(), "Cannot parse coordinate!");
        return element;
    }

    public Optional<String> getGroup() {
        return group;
    }


    public String getName() {
        return name;
    }

    public Optional<String> getVersion() {
        return version;
    }

    public Optional<String> getClassifier() {
        return classifier;
    }

    public Optional<String> getExtension() {
        return extension;
    }

    public static class CoordinateBuilder {
        private Optional<String> group;
        private String name;
        private Optional<String> version;
        private Optional<String> classifier;
        private Optional<String> extension;

        private CoordinateBuilder() {
        }

        public static CoordinateBuilder aCoordinate(String name) {
            CoordinateBuilder coordinateBuilder = new CoordinateBuilder();
            coordinateBuilder.name = name;
            return coordinateBuilder;
        }

        public CoordinateBuilder withGroup(String group) {
            this.group = Optional.of(group);
            return this;
        }

        public CoordinateBuilder withVersion(String version) {
            this.version = Optional.of(version);
            return this;
        }

        public CoordinateBuilder withClassifier(String classifier) {
            this.classifier = Optional.of(classifier);
            return this;
        }

        public CoordinateBuilder withExtension(String extension) {
            this.extension = Optional.of(extension);
            return this;
        }

        public Coordinate build() {
            Coordinate coordinate = new Coordinate(group, name, version, classifier, extension);
            return coordinate;
        }
    }
}
