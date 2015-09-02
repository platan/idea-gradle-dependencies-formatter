package com.github.platan.idea.dependencies;

import com.github.platan.idea.dependencies.gradle.Dependency;
import com.github.platan.idea.dependencies.gradle.GradleDependenciesSerializer;
import com.github.platan.idea.dependencies.maven.DependencyValidationException;
import com.github.platan.idea.dependencies.maven.MavenDependenciesDeserializer;
import com.github.platan.idea.dependencies.maven.MavenDependency;
import com.github.platan.idea.dependencies.maven.MavenToGradleMapper;
import com.github.platan.idea.dependencies.maven.UnsupportedContentException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

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
        } catch (UnsupportedContentException e) {
            return mavenDependencyXml;
        } catch (DependencyValidationException e) {
            return mavenDependencyXml;
        }
        List<Dependency> dependencies = Lists.transform(mavenDependencies, new Function<MavenDependency, Dependency>() {
            @Nullable
            @Override
            public Dependency apply(MavenDependency mavenDependency) {
                return mavenToGradleMapper.map(mavenDependency);
            }
        });
        return gradleDependencySerializer.serialize(dependencies);
    }

}
