package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GradleDependenciesSerializerImpl implements GradleDependenciesSerializer {

    private static final Joiner NEW_LINE_JOINER = Joiner.on('\n');

    private static final Function<Dependency, String> TO_GRADLE_DEPENDENCY_FUNCTION = new Function<Dependency, String>() {
        @NotNull
        @Override
        public String apply(@NotNull Dependency dependency) {
            if (useClosure(dependency)) {
                return String.format("%s(%s) {\n%s}",
                        dependency.getConfiguration(), stringNotation(dependency), getClosureContent(dependency));
            }
            return String.format("%s %s", dependency.getConfiguration(), stringNotation(dependency));
        }

        private boolean useClosure(Dependency dependency) {
            List<Exclusion> exclusions = dependency.getExclusions();
            return !exclusions.isEmpty() || !dependency.isTransitive();
        }

    };

    private static String getClosureContent(Dependency dependency) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Exclusion exclusion : dependency.getExclusions()) {
            stringBuilder.append(String.format("\texclude group: '%s', module: '%s'", exclusion.getGroup(), exclusion.getModule()));
            stringBuilder.append('\n');
        }
        if (!dependency.isTransitive()) {
            stringBuilder.append("\ttransitive = false\n");
        }
        return stringBuilder.toString();
    }

    private static String stringNotation(Dependency dependency) {
        return String.format("'%s:%s:%s'", dependency.getGroup(), dependency.getName(),
                dependency.getVersion());
    }


    @NotNull
    @Override
    public String serialize(@NotNull List<Dependency> dependencies) {
        Iterable<String> gradleDependencies = Iterables.transform(dependencies, TO_GRADLE_DEPENDENCY_FUNCTION);
        return NEW_LINE_JOINER.join(gradleDependencies);
    }
}
