package com.github.platan.idea.dependencies.maven;

import com.github.platan.idea.dependencies.gradle.Dependency;
import com.github.platan.idea.dependencies.gradle.Exclusion;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MavenToGradleMapperImpl implements MavenToGradleMapper {

    private static final Function<MavenExclusion, Exclusion> MAVEN_EXCLUSION_TO_EXCLUSION_FUNCTION =
            new Function<MavenExclusion, Exclusion>() {
                @NotNull
                @Override
                public Exclusion apply(MavenExclusion mavenExclusion) {
                    return new Exclusion(mavenExclusion.getGroupId(), mavenExclusion.getArtifactId());
                }
            };
    private static final String ASTERISK = "*";
    private static final Predicate<MavenExclusion> IS_WILDCARD_EXCLUDE = new Predicate<MavenExclusion>() {
        @Override
        public boolean apply(MavenExclusion mavenExclusion) {
            return mavenExclusion.getGroupId().equals(ASTERISK) && mavenExclusion.getArtifactId().equals(ASTERISK);
        }
    };
    private static final String SYSTEM_PATH = "systemPath";
    private static final String TYPE = "type";
    private static final String TRUE = "true";

    @Override
    @NotNull
    public Dependency map(@NotNull final MavenDependency mavenDependency) {
        List<Exclusion> excludes = Lists.transform(mavenDependency.getExclusions(), MAVEN_EXCLUSION_TO_EXCLUSION_FUNCTION);
        boolean hasWildcardExclude = Iterables.removeIf(mavenDependency.getExclusions(), IS_WILDCARD_EXCLUDE);
        boolean transitive = !hasWildcardExclude;
        Map<String, String> extraOptions = createExtraOptions(mavenDependency);
        boolean optional = isOptional(mavenDependency);
        return new Dependency(mavenDependency.getGroupId(), mavenDependency.getArtifactId(), mavenDependency.getVersion(),
                mavenDependency.getClassifier(), getScope(mavenDependency.getScope()), excludes, transitive,
                extraOptions, optional);
    }

    private HashMap<String, String> createExtraOptions(MavenDependency mavenDependency) {
        HashMap<String, String> extraOptions = new HashMap<>();
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
