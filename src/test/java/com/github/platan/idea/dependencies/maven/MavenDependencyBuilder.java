package com.github.platan.idea.dependencies.maven;

import java.util.ArrayList;
import java.util.List;

public class MavenDependencyBuilder {
    private String groupId;
    private String artifactId;
    private String version;
    private Scope scope = Scope.COMPILE;
    private List<MavenExclusion> exclusionList = new ArrayList<MavenExclusion>();

    public static MavenDependencyBuilder aMavenDependency(String groupId, String artifactId, String version) {
        return new MavenDependencyBuilder().withGroupId(groupId).withArtifactId(artifactId).withVersion(version);
    }

    public MavenDependencyBuilder withGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public MavenDependencyBuilder withArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public MavenDependencyBuilder withVersion(String version) {
        this.version = version;
        return this;
    }

    public MavenDependencyBuilder withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

    public MavenDependencyBuilder withExclusionList(List<MavenExclusion> exclusionList) {
        this.exclusionList = exclusionList;
        return this;
    }

    public MavenDependency build() {
        MavenDependency mavenDependency = new MavenDependency();
        mavenDependency.setGroupId(groupId);
        mavenDependency.setArtifactId(artifactId);
        mavenDependency.setVersion(version);
        mavenDependency.setScope(scope);
        mavenDependency.setExclusions(exclusionList);
        return mavenDependency;
    }
}
