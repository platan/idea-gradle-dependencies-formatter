package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Objects;

public final class Exclusion {

    private final String group;
    private final String module;

    public Exclusion(String group, String module) {
        this.group = group;
        this.module = module;
    }

    public String getGroup() {
        return group;
    }

    public String getModule() {
        return module;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(group, module);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Exclusion other = (Exclusion) obj;
        return Objects.equal(this.group, other.group)
                && Objects.equal(this.module, other.module);
    }

    @Override
    public String toString() {
        return "Exclusion{"
                + "group='" + group + '\''
                + ", module='" + module + '\''
                + '}';
    }

}
