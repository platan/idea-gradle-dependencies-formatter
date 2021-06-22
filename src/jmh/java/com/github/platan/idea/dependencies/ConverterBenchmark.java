package com.github.platan.idea.dependencies;

import com.github.platan.idea.dependencies.gradle.GradleDependenciesSerializerImpl;
import com.github.platan.idea.dependencies.maven.MavenDependenciesDeserializerImpl;
import com.github.platan.idea.dependencies.maven.MavenToGradleMapperImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class ConverterBenchmark {

    private final MavenToGradleConverter mavenToGradleConverter =
            new MavenToGradleConverter(new MavenDependenciesDeserializerImpl(),
                    new GradleDependenciesSerializerImpl(), new MavenToGradleMapperImpl());
    private static final String MAVEN_DEPENDENCY = "<dependency>\n"
            + "                <groupId>org.spockframework</groupId>\n"
            + "        <artifactId>spock-core</artifactId>\n"
            + "        <version>1.0-groovy-2.4</version>\n"
            + "        </dependency>";

    @Benchmark
    public void convertBenchmark() {
        mavenToGradleConverter.convert(MAVEN_DEPENDENCY);
    }
}
