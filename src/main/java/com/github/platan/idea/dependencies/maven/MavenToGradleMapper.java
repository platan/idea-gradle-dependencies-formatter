package com.github.platan.idea.dependencies.maven;

import com.github.platan.idea.dependencies.gradle.Dependency;
import org.jetbrains.annotations.NotNull;

public interface MavenToGradleMapper {
    @NotNull
    Dependency map(@NotNull MavenDependency mavenDependency);
}
