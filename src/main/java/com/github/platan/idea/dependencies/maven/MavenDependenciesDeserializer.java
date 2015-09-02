package com.github.platan.idea.dependencies.maven;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MavenDependenciesDeserializer {
    @NotNull
    List<MavenDependency> deserialize(@NotNull String mavenDependencyXml) throws UnsupportedContentException;
}
