package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;

public class GradleDependenciesSerializerImpl implements GradleDependenciesSerializer {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final Joiner NEW_LINE_JOINER = Joiner.on(NEW_LINE);
    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final Function<Map.Entry<String, String>, String> EXTRA_OPTION_FORMATTER = new Function<Map.Entry<String, String>,
            String>() {
        @Nullable
        @Override
        public String apply(Map.Entry<String, String> extraOption) {
            return String.format("%s = %s (%s is not supported)", extraOption.getKey(), extraOption.getValue(), extraOption.getKey());
        }
    };
    private static final Function<Dependency, String> FORMAT_GRADLE_DEPENDENCY = new Function<Dependency, String>() {
        @NotNull
        @Override
        public String apply(@NotNull Dependency dependency) {
            String comment = "";
            if (dependency.hasExtraOptions()) {
                comment = createComment(dependency.getExtraOptions());
            }
            if (useClosure(dependency)) {
                return String.format("%s(%s) {%s%s%s}",
                        dependency.getConfiguration(), toStringNotation(dependency), comment, NEW_LINE, getClosureContent(dependency));
            }
            return String.format("%s %s%s", dependency.getConfiguration(), toStringNotation(dependency), comment);
        }

        private String createComment(Map<String, String> extraOptions) {
            return String.format(" // %s", COMMA_JOINER.join(transform(extraOptions.entrySet(), EXTRA_OPTION_FORMATTER)));
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
            stringBuilder.append(NEW_LINE);
        }
        if (!dependency.isTransitive()) {
            stringBuilder.append("\ttransitive = false");
            stringBuilder.append(NEW_LINE);
        }
        return stringBuilder.toString();
    }

    private static String toStringNotation(Dependency dependency) {
        StringBuilder result = new StringBuilder();
        result.append('\'');
        result.append(dependency.getGroup());
        result.append(':');
        result.append(dependency.getName());
        appendIf(dependency.getVersion(), result, dependency.hasVersion());
        appendIf(dependency.getClassifier(), result, dependency.hasClassifier());
        result.append('\'');
        return result.toString();
    }

    private static void appendIf(String value, StringBuilder result, boolean shouldAppend) {
        if (shouldAppend) {
            result.append(':');
            result.append(value);
        }
    }

    @NotNull
    @Override
    public String serialize(@NotNull List<Dependency> dependencies) {
        return NEW_LINE_JOINER.join(transform(dependencies, FORMAT_GRADLE_DEPENDENCY));
    }
}
