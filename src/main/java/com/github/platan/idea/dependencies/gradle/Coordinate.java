package com.github.platan.idea.dependencies.gradle;

import static com.google.common.collect.Iterables.transform;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Coordinate implements Comparable<Coordinate> {
    private static final String NAME_KEY = "name";
    private static final String GROUP_KEY = "group";
    private static final String VERSION_KEY = "version";
    private static final String CLASSIFIER_KEY = "classifier";
    private static final String EXT_KEY = "ext";
    private static final Set<String> ALL_KEYS = ImmutableSet.of(GROUP_KEY, NAME_KEY, VERSION_KEY, CLASSIFIER_KEY, EXT_KEY);
    private static final Set<String> REQUIRED_KEYS = ImmutableSet.of(GROUP_KEY, NAME_KEY);
    private static final Splitter ON_SEMICOLON_SPLITTER = Splitter.onPattern(":").limit(4);
    private static final Joiner ON_COMMA_SPACE_JOINER = Joiner.on(", ");
    private static final Comparator<String> COMPARATOR = new NaturalNullFirstOrdering<String>();
    private final String group;
    private final String name;
    private final String version;
    private final String classifier;
    private final String extension;

    public Coordinate(@Nullable String group, String name, @Nullable String version, @Nullable String classifier,
                      @Nullable String extension) {
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
        return Optional.fromNullable(group);
    }


    public String getName() {
        return name;
    }

    public Optional<String> getVersion() {
        return Optional.fromNullable(version);
    }

    public Optional<String> getClassifier() {
        return Optional.fromNullable(classifier);
    }

    public Optional<String> getExtension() {
        return Optional.fromNullable(extension);
    }

    public String toMapNotation(String quote) {
        return ON_COMMA_SPACE_JOINER.join(transform(toMap().entrySet(), new MapEntryToStringFunction(quote)));
    }

    public String toStringNotation() {
        StringBuilder stringBuilder = new StringBuilder();
        if (group != null) {
            stringBuilder.append(group);
        }
        stringBuilder.append(':');
        stringBuilder.append(name);
        appendIfNotNull(stringBuilder, ':', version);
        if (version == null && classifier != null) {
            stringBuilder.append(':');
        }
        appendIfNotNull(stringBuilder, ':', classifier);
        appendIfNotNull(stringBuilder, '@', extension);
        return stringBuilder.toString();
    }

    private void appendIfNotNull(StringBuilder stringBuilder, char separator, String nullabeValue) {
        if (nullabeValue != null) {
            stringBuilder.append(separator);
            stringBuilder.append(nullabeValue);
        }
    }

    public static Coordinate fromMap(Map<String, String> map) {
        String name = map.get(NAME_KEY);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "'name' element is required. ");
        CoordinateBuilder coordinateBuilder = CoordinateBuilder.aCoordinate(name);
        if (!Strings.isNullOrEmpty(map.get(GROUP_KEY))) {
            coordinateBuilder.withGroup(map.get(GROUP_KEY));
        }
        if (!Strings.isNullOrEmpty(map.get(VERSION_KEY))) {
            coordinateBuilder.withVersion(map.get(VERSION_KEY));
        }
        if (!Strings.isNullOrEmpty(map.get(CLASSIFIER_KEY))) {
            coordinateBuilder.withClassifier(map.get(CLASSIFIER_KEY));
        }
        if (!Strings.isNullOrEmpty(map.get(EXT_KEY))) {
            coordinateBuilder.withExtension(map.get(EXT_KEY));
        }
        return coordinateBuilder.build();
    }

    private Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        putIfNotNull(map, group, GROUP_KEY);
        map.put(NAME_KEY, name);
        putIfNotNull(map, version, VERSION_KEY);
        putIfNotNull(map, classifier, CLASSIFIER_KEY);
        putIfNotNull(map, extension, EXT_KEY);
        return map;
    }

    private void putIfNotNull(Map<String, String> map, String value, String key) {
        if (value != null) {
            map.put(key, value);
        }
    }

    public static boolean isValidMap(Map<String, String> map) {
        return Sets.difference(map.keySet(), ALL_KEYS).isEmpty() && map.keySet().containsAll(REQUIRED_KEYS);
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

    @Override
    public int compareTo(Coordinate that) {
        return ComparisonChain.start()
                .compare(this.group, that.group, COMPARATOR)
                .compare(this.name, that.name)
                .compare(this.version, that.version, COMPARATOR)
                .compare(this.classifier, that.classifier, COMPARATOR)
                .compare(this.extension, that.extension, COMPARATOR)
                .result();
    }

    private static final class NaturalNullFirstOrdering<T extends Comparable<? super T>> implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            if (o1 != null && o2 != null) {
                return o1.compareTo(o2);
            }
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 != null) {
                return 1;
            } else {
                return -1;
            }
        }
    }


    public static class CoordinateBuilder {
        private String group = null;
        private String name;
        private String version = null;
        private String classifier = null;

        private String extension = null;

        private CoordinateBuilder() {
        }

        public static CoordinateBuilder aCoordinate(String name) {
            CoordinateBuilder coordinateBuilder = new CoordinateBuilder();
            coordinateBuilder.name = name;
            return coordinateBuilder;
        }

        public CoordinateBuilder withGroup(String group) {
            this.group = emptyToNull(group);
            return this;
        }

        private String emptyToNull(String value) {
            return value == null || value.isEmpty() ? null : value;
        }

        public CoordinateBuilder withVersion(String version) {
            this.version = emptyToNull(version);
            return this;
        }

        public CoordinateBuilder withClassifier(String classifier) {
            this.classifier = emptyToNull(classifier);
            return this;
        }

        public CoordinateBuilder withExtension(String extension) {
            this.extension = emptyToNull(extension);
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
