package com.github.platan.idea.dependencies.gradle;


import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;

public final class Dependency {

    private final String group;
    private final String name;
    private final String version;
    private final String configuration;
    private final List<Exclusion> exclusions;
    private final boolean transitive;

    public Dependency(String group, String name, String version, String configuration, List<Exclusion> exclusions) {
        this(group, name, version, configuration, exclusions, true);
    }

    public Dependency(String group, String name, String version, String configuration, List<Exclusion> exclusions, boolean transitive) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.configuration = configuration;
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

    public String getConfiguration() {
        return configuration;
    }

    public List<Exclusion> getExclusions() {
        return exclusions;
    }

    public boolean isTransitive() {
        return transitive;
    }

    public boolean hasVersion() {
        return version != null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(group, name, version, configuration, exclusions, transitive);
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
                && Objects.equal(this.exclusions, other.exclusions)
                && Objects.equal(this.transitive, other.transitive);
    }

    @Override
    public String toString() {
        return "Dependency{"
                + "group='" + group + '\''
                + ", name='" + name + '\''
                + ", version='" + version + '\''
                + ", configuration='" + configuration + '\''
                + ", exclusions=" + exclusions
                + ", transitive=" + transitive
                + '}';
    }

}
