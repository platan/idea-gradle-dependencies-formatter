package com.github.platan.idea.dependencies.gradle;

import static com.google.common.collect.Iterables.transform;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Coordinate {
    private static final Splitter ON_SEMICOLON_SPLITTER = Splitter.on(":").limit(4);
    private static final Splitter ON_AT_SPLITTER = Splitter.on("@").limit(2);
    private static final Joiner ON_COMMA_SPACE_JOINER = Joiner.on(", ");
    private static final Function<Map.Entry<String, String>, String> MAP_ENTRY_TO_STRING =
            new Function<Map.Entry<String, String>, String>() {
                @Override
                public String apply(Map.Entry<String, String> mapEntry) {
                    return String.format("%s: '%s'", mapEntry.getKey(), mapEntry.getValue());
                }
            };
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
            List<String> classifierPlusExtension = Lists.newArrayList(ON_AT_SPLITTER.split(getAt(parts, 3)));
            if (classifierPlusExtension.size() >= 1) {
                coordinateBuilder.withClassifier(getAt(classifierPlusExtension, 0));
            }
            if (classifierPlusExtension.size() == 2) {
                coordinateBuilder.withExtension(getAt(classifierPlusExtension, 1));
            }
        }
        return coordinateBuilder.build();
    }

    public static boolean isStringNotationCoordinate(String stringNotation) {
        return Pattern.compile("[^:\\s]+:[^:\\s]+(:[^:\\s]*)?(:[^:\\s]+)?(@[^:\\s]+)?").matcher(stringNotation).matches();
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

    public String toMapNotation() {
        return ON_COMMA_SPACE_JOINER.join(transform(toMap().entrySet(), MAP_ENTRY_TO_STRING));
    }

    private Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        putIfPresent(map, group, "group");
        map.put("name", name);
        putIfPresent(map, version, "version");
        putIfPresent(map, classifier, "classifier");
        putIfPresent(map, extension, "ext");
        return map;
    }

    private void putIfPresent(Map<String, String> map, Optional<String> value, String key) {
        if (value.isPresent()) {
            map.put(key, value.get());
        }
    }

    public static class CoordinateBuilder {
        private Optional<String> group = Optional.absent();
        private String name;
        private Optional<String> version = Optional.absent();
        private Optional<String> classifier = Optional.absent();
        private Optional<String> extension = Optional.absent();

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
            return new Coordinate(group, name, version, classifier, extension);
        }
    }
}
