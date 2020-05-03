package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.jetbrains.annotations.Nullable;

public abstract class BaseCoordinate<T> {
    protected static final String NAME_KEY = "name";
    protected static final String GROUP_KEY = "group";
    protected static final String VERSION_KEY = "version";
    protected static final String CLASSIFIER_KEY = "classifier";
    protected static final String EXT_KEY = "ext";

    protected final T group;
    protected final T name;
    protected final T version;
    protected final T classifier;
    protected final T extension;

    public BaseCoordinate(@Nullable T group, T name, @Nullable T version, @Nullable T classifier,
                          @Nullable T extension) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.classifier = classifier;
        this.extension = extension;
    }

    public Optional<T> getGroup() {
        return Optional.fromNullable(group);
    }

    public T getName() {
        return name;
    }

    public Optional<T> getVersion() {
        return Optional.fromNullable(version);
    }

    public Optional<T> getClassifier() {
        return Optional.fromNullable(classifier);
    }

    public Optional<T> getExtension() {
        return Optional.fromNullable(extension);
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
        final BaseCoordinate<?> other = (BaseCoordinate<?>) obj;
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
                + ", name=" + name
                + ", version=" + version
                + ", classifier=" + classifier
                + ", extension=" + extension
                + '}';
    }

}
