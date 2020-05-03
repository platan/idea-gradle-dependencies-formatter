package com.github.platan.idea.dependencies.gradle;


import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public final class Dependency {

    private final String group;
    private final String name;
    private final String version;
    private final String classifier;
    private final String configuration;
    private final List<Exclusion> exclusions;
    private final boolean optional;
    private final boolean transitive;
    private final Map<String, String> extraOptions;

    public Dependency(String group, String name, String version, String classifier, String configuration, List<Exclusion>
            exclusions, boolean transitive, Map<String, String> extraOptions, boolean optional) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.classifier = classifier;
        this.configuration = configuration;
        this.optional = optional;
        this.extraOptions = ImmutableMap.copyOf(extraOptions);
        this.exclusions = ImmutableList.copyOf(exclusions);
        this.transitive = transitive;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getConfiguration() {
        return configuration;
    }

    public List<Exclusion> getExclusions() {
        return exclusions;
    }

    public Map<String, String> getExtraOptions() {
        return extraOptions;
    }

    public boolean hasExtraOptions() {
        return !extraOptions.isEmpty();
    }

    public boolean isTransitive() {
        return transitive;
    }

    public boolean hasVersion() {
        return version != null;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(group, name, version, classifier, configuration, exclusions, extraOptions, optional, transitive);
    }

    public boolean hasClassifier() {
        return classifier != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Dependency other = (Dependency) obj;
        return Objects.equal(this.group, other.group)
                && Objects.equal(this.name, other.name)
                && Objects.equal(this.version, other.version)
                && Objects.equal(this.configuration, other.configuration)
                && Objects.equal(this.classifier, other.classifier)
                && Objects.equal(this.exclusions, other.exclusions)
                && Objects.equal(this.extraOptions, other.extraOptions)
                && Objects.equal(this.optional, other.optional)
                && Objects.equal(this.transitive, other.transitive);
    }

    @Override
    public String toString() {
        return "Dependency{"
                + "group='" + group + '\''
                + ", name='" + name + '\''
                + ", version='" + version + '\''
                + ", classifier=" + classifier
                + ", configuration='" + configuration + '\''
                + ", exclusions=" + exclusions
                + ", optional=" + optional
                + ", transitive=" + transitive
                + ", extraOptions=" + extraOptions
                + '}';
    }


}
