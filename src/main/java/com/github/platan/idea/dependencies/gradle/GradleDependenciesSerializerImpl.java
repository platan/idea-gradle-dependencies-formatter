package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.google.common.collect.Iterables.transform;

public class GradleDependenciesSerializerImpl implements GradleDependenciesSerializer {

    private static final Joiner NEW_LINE_JOINER = Joiner.on('\n');

    private static final Function<Dependency, String> FORMAT_GRADLE_DEPENDENCY = new Function<Dependency, String>() {
        @NotNull
        @Override
        public String apply(@NotNull Dependency dependency) {
            if (useClosure(dependency)) {
                return String.format("%s(%s) {\n%s}",
                        dependency.getConfiguration(), toStringNotation(dependency), getClosureContent(dependency));
            }
            return String.format("%s %s", dependency.getConfiguration(), toStringNotation(dependency));
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

    private static String toStringNotation(Dependency dependency) {
        String groupAndName = String.format("%s:%s", dependency.getGroup(), dependency.getName());
        String result;
        if (dependency.hasVersion()) {
            result = String.format("%s:%s", groupAndName, dependency.getVersion());
        } else {
            result = groupAndName;
        }
        return String.format("'%s'", result);
    }

    @NotNull
    @Override
    public String serialize(@NotNull List<Dependency> dependencies) {
        return NEW_LINE_JOINER.join(transform(dependencies, FORMAT_GRADLE_DEPENDENCY));
    }
}
