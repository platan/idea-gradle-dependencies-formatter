package com.github.platan.idea.dependencies;

import com.github.platan.idea.dependencies.gradle.Dependency;
import com.github.platan.idea.dependencies.gradle.GradleDependenciesSerializer;
import com.github.platan.idea.dependencies.maven.DependencyValidationException;
import com.github.platan.idea.dependencies.maven.MavenDependenciesDeserializer;
import com.github.platan.idea.dependencies.maven.MavenDependency;
import com.github.platan.idea.dependencies.maven.MavenToGradleMapper;
import com.github.platan.idea.dependencies.maven.UnsupportedContentException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class MavenToGradleConverter {

    private final MavenDependenciesDeserializer mavenDependencyParser;
    private final GradleDependenciesSerializer gradleDependencySerializer;
    private final MavenToGradleMapper mavenToGradleMapper;

    public MavenToGradleConverter(MavenDependenciesDeserializer mavenDependencyParser, GradleDependenciesSerializer
            gradleDependencySerializer, MavenToGradleMapper mavenToGradleMapper) {
        this.mavenDependencyParser = mavenDependencyParser;
        this.gradleDependencySerializer = gradleDependencySerializer;
        this.mavenToGradleMapper = mavenToGradleMapper;
    }

    @NotNull
    public String convert(@NotNull String mavenDependencyXml) {
        List<MavenDependency> mavenDependencies;
        try {
            mavenDependencies = mavenDependencyParser.deserialize(mavenDependencyXml);
            if (mavenDependencies.isEmpty()) {
                return mavenDependencyXml;
            }
        } catch (UnsupportedContentException | DependencyValidationException e) {
            return mavenDependencyXml;
        }
        List<Dependency> dependencies = mavenDependencies.stream().map(mavenToGradleMapper::map).collect(toList());
        return gradleDependencySerializer.serialize(dependencies);
    }

}
