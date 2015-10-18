package com.github.platan.idea.dependencies.gradle;

import static com.google.common.collect.Iterables.transform;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Coordinate {
    private static final Splitter ON_SEMICOLON_SPLITTER = Splitter.onPattern(":").limit(4);
    private static final Joiner ON_COMMA_SPACE_JOINER = Joiner.on(", ");
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
        Preconditions.checkArgument(numberOfParts > 1, "Cannot parse coordinate!");
        CoordinateBuilder coordinateBuilder = CoordinateBuilder.aCoordinate(getNotEmptyAt(parts, 1)).withGroup(getAt(parts, 0));
        if (numberOfParts >= 3) {
            coordinateBuilder.withVersion(getAt(parts, 2));
        }
        if (numberOfParts == 4) {
            coordinateBuilder.withClassifier(getAt(parts, 3));
        }
        String last = parts.get(parts.size() - 1);
        if (last.contains("@")) {
            coordinateBuilder.withExtension(last.split("@", 2)[1]);
        }
        return coordinateBuilder.build();
    }

    public static boolean isStringNotationCoordinate(String stringNotation) {
        return Pattern.compile("[^:\\s]*:[^:\\s]+(:[^:\\s]*)?(:[^:\\s]+)?(@[^:\\s]+)?").matcher(stringNotation).matches();
    }

    private static String getNotEmptyAt(List<String> list, int index) {
        Preconditions.checkArgument(!list.get(index).trim().isEmpty(), "Cannot parse coordinate!");
        return getAt(list, index);
    }

    private static String getAt(List<String> list, int index) {
        String element = list.get(index);
        if (isLast(list, index) && element.contains("@")) {
            return element.split("@", 2)[0];
        }
        return element;
    }

    private static boolean isLast(List<String> list, int index) {
        return list.size() == index + 1;
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

    public String toMapNotation(String quote) {
        return ON_COMMA_SPACE_JOINER.join(transform(toMap().entrySet(), new MapEntryToStringFunction(quote)));
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
        final Coordinate other = (Coordinate) obj;
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
                + ", name='" + name + '\''
                + ", version=" + version
                + ", classifier=" + classifier
                + ", extension=" + extension
                + '}';
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
            this.group = optionalOf(group);
            return this;
        }

        @NotNull
        private Optional<String> optionalOf(String group) {
            return group.isEmpty() ? Optional.<String>absent() : Optional.of(group);
        }

        public CoordinateBuilder withVersion(String version) {
            this.version = optionalOf(version);
            return this;
        }

        public CoordinateBuilder withClassifier(String classifier) {
            this.classifier = optionalOf(classifier);
            return this;
        }

        public CoordinateBuilder withExtension(String extension) {
            this.extension = optionalOf(extension);
            return this;
        }

        public Coordinate build() {
            return new Coordinate(group, name, version, classifier, extension);
        }
    }

    private static class MapEntryToStringFunction implements Function<Map.Entry<String, String>, String> {
        private final String quotationMark;

        private MapEntryToStringFunction(String quotationMark) {
            this.quotationMark = quotationMark;
        }

        @Override
        public String apply(Map.Entry<String, String> mapEntry) {
            return String.format("%s: %s%s%s", mapEntry.getKey(), quotationMark, mapEntry.getValue(), quotationMark);
        }
    }
}
