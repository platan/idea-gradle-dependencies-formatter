package com.github.platan.idea.dependencies.gradle;

import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class GradleDependenciesSerializerImpl implements GradleDependenciesSerializer {

    // https://github.com/platan/idea-gradle-dependencies-formatter/issues/3
    // We should use \n as a new line separator in texts passed to a editor
    // See: http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/documents.html
    private static final char NEW_LINE = '\n';
    private static final Joiner NEW_LINE_JOINER = Joiner.on(NEW_LINE);
    private static final String COMMA_SPACE = ", ";
    private static final Function<Dependency, String> FORMAT_GRADLE_DEPENDENCY = new Function<Dependency, String>() {
        @NotNull
        @Override
        public String apply(@NotNull Dependency dependency) {
            String comment = "";
            if (dependency.hasExtraOptions()) {
                comment = createComment(dependency.getExtraOptions());
            }
            if (useClosure(dependency)) {
                if (dependency.isOptional()) {
                    comment += prepareComment(comment, "optional = true (optional is not supported for dependency with closure)");
                }
                return String.format("%s(%s) {%s%s%s}",
                        dependency.getConfiguration(), toStringNotation(dependency), comment, NEW_LINE, getClosureContent(dependency));
            }
            String optional = dependency.isOptional() ? ", optional" : "";
            return String.format("%s %s%s%s", dependency.getConfiguration(), toStringNotation(dependency), optional, comment);
        }

        private String prepareComment(String comment, String text) {
            return comment.isEmpty() ? String.format(" // %s", text) : String.format(", %s", text);
        }

        private String createComment(Map<String, String> extraOptions) {
            String comment = extraOptions.entrySet().stream()
                    .map(extraOption ->
                            String.format("%s = %s (%s is not supported)", extraOption.getKey(), extraOption.getValue(), extraOption.getKey()))
                    .collect(joining(COMMA_SPACE));
            return String.format(" // %s", comment);
        }

        private boolean useClosure(Dependency dependency) {
            List<Exclusion> exclusions = dependency.getExclusions();
            return !exclusions.isEmpty() || !dependency.isTransitive();
        }

        private String toStringNotation(Dependency dependency) {
            char quotationMark = dependency.getVersion() != null && dependency.getVersion().contains("${") ? '"' : '\'';
            StringBuilder result = new StringBuilder();
            result.append(quotationMark);
            result.append(dependency.getGroup());
            result.append(':');
            result.append(dependency.getName());
            appendIf(dependency.getVersion(), result, dependency.hasVersion());
            appendIf(dependency.getClassifier(), result, dependency.hasClassifier());
            result.append(quotationMark);
            return result.toString();
        }

        private String getClosureContent(Dependency dependency) {
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

        private void appendIf(String value, StringBuilder result, boolean shouldAppend) {
            if (shouldAppend) {
                result.append(':');
                result.append(value);
            }
        }

    };

    @NotNull
    @Override
    public String serialize(@NotNull List<Dependency> dependencies) {
        return NEW_LINE_JOINER.join(dependencies.stream().map(FORMAT_GRADLE_DEPENDENCY).collect(toList()));
    }
}
