package com.github.platan.idea.dependencies.maven;

import com.github.platan.idea.dependencies.gradle.Dependency;
import com.github.platan.idea.dependencies.gradle.Exclusion;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class MavenToGradleMapperImpl implements MavenToGradleMapper {

    private static final String ASTERISK = "*";
    private static final Predicate<MavenExclusion> IS_WILDCARD_EXCLUDE = mavenExclusion ->
            mavenExclusion.getGroupId().equals(ASTERISK) && mavenExclusion.getArtifactId().equals(ASTERISK);
    private static final String SYSTEM_PATH = "systemPath";
    private static final String TYPE = "type";
    private static final String TRUE = "true";

    @Override
    @NotNull
    public Dependency map(@NotNull final MavenDependency mavenDependency) {
        boolean hasWildcardExclude = mavenDependency.getExclusions().stream()
                .anyMatch(IS_WILDCARD_EXCLUDE);
        List<Exclusion> excludes = mavenDependency.getExclusions().stream()
                .filter(IS_WILDCARD_EXCLUDE.negate())
                .map(mavenExclusion -> new Exclusion(mavenExclusion.getGroupId(), mavenExclusion.getArtifactId()))
                .collect(toList());
        boolean transitive = !hasWildcardExclude;
        Map<String, String> extraOptions = createExtraOptions(mavenDependency);
        boolean optional = isOptional(mavenDependency);
        return new Dependency(mavenDependency.getGroupId(), mavenDependency.getArtifactId(), mavenDependency.getVersion(),
                mavenDependency.getClassifier(), getScope(mavenDependency.getScope()), excludes, transitive,
                extraOptions, optional);
    }

    private HashMap<String, String> createExtraOptions(MavenDependency mavenDependency) {
        HashMap<String, String> extraOptions = new LinkedHashMap<>();
        if (mavenDependency.getSystemPath() != null) {
            extraOptions.put(SYSTEM_PATH, mavenDependency.getSystemPath());
        }
        if (mavenDependency.getType() != null) {
            extraOptions.put(TYPE, mavenDependency.getType());
        }
        return extraOptions;
    }

    private boolean isOptional(MavenDependency mavenDependency) {
        return isEqualTo(mavenDependency.getOptional(), TRUE);
    }

    private boolean isEqualTo(String string1, String string2) {
        return string1 != null && string1.compareTo(string2) == 0;
    }

    private String getScope(Scope scope) {
        if (scope == Scope.TEST) {
            return "testCompile";
        } else {
            return scope.toString().toLowerCase();
        }
    }
}
