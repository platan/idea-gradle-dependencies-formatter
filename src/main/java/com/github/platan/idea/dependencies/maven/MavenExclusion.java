package com.github.platan.idea.dependencies.maven;

import com.google.common.base.Objects;

public class MavenExclusion {

    private String groupId;
    private String artifactId;

    public MavenExclusion() {
    }

    public MavenExclusion(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId, artifactId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MavenExclusion other = (MavenExclusion) obj;
        return Objects.equal(this.groupId, other.groupId)
                && Objects.equal(this.artifactId, other.artifactId);
    }

    @Override
    public String toString() {
        return "MavenExclusion{"
                + "groupId='" + groupId + '\''
                + ", artifactId='" + artifactId + '\''
                + '}';
    }

}
