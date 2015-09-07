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
    private String classifier;
    private Scope scope = Scope.COMPILE;
    private String systemPath;
    private String type;
    @XmlElement(name = "exclusion")
    @XmlElementWrapper(name = "exclusions")
    private List<MavenExclusion> exclusionList = new ArrayList<MavenExclusion>();


    public MavenDependency() {
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

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getSystemPath() {
        return systemPath;
    }

    public void setSystemPath(String systemPath) {
        this.systemPath = systemPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId, artifactId, version, classifier, scope, systemPath, type, exclusionList);
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
                && Objects.equal(this.classifier, other.classifier)
                && Objects.equal(this.scope, other.scope)
                && Objects.equal(this.systemPath, other.systemPath)
                && Objects.equal(this.type, other.type)
                && Objects.equal(this.exclusionList, other.exclusionList);
    }

    @Override
    public String toString() {
        return "MavenDependency{"
                + "groupId='" + groupId + '\''
                + ", artifactId='" + artifactId + '\''
                + ", version='" + version + '\''
                + ", classifier='" + classifier + '\''
                + ", scope=" + scope
                + ", systemPath='" + systemPath + '\''
                + ", type='" + type + '\''
                + ", exclusionList=" + exclusionList
                + '}';
    }
}
