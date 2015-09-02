package com.github.platan.idea.dependencies.maven;


import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "dependency")
@XmlAccessorType(XmlAccessType.FIELD)
public class MavenDependency {

    private String groupId;
    private String artifactId;
    private String version;
    private Scope scope = Scope.COMPILE;
    @XmlElement(name = "exclusion")
    @XmlElementWrapper(name = "exclusions")
    private List<MavenExclusion> exclusionList = new ArrayList<MavenExclusion>();

    public MavenDependency() {
    }

    public MavenDependency(String groupId, String artifactId, String version, Scope scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
    }

    public MavenDependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public MavenDependency(String groupId, String artifactId, String version, List<MavenExclusion> exclusions) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.exclusionList = exclusions;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public Scope getScope() {
        return scope;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public List<MavenExclusion> getExclusions() {
        return exclusionList;
    }

    public void setExclusions(List<MavenExclusion> exclusions) {
        this.exclusionList = exclusions;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId, artifactId, version, scope, exclusionList);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MavenDependency other = (MavenDependency) obj;
        return Objects.equal(this.groupId, other.groupId)
                && Objects.equal(this.artifactId, other.artifactId)
                && Objects.equal(this.version, other.version)
                && Objects.equal(this.scope, other.scope)
                && Objects.equal(this.exclusionList, other.exclusionList);
    }

    @Override
    public String toString() {
        return "MavenDependency{"
                + "groupId='" + groupId + '\''
                + ", artifactId='" + artifactId + '\''
                + ", version='" + version + '\''
                + ", scope=" + scope
                + ", exclusionList=" + exclusionList
                + '}';
    }

}
