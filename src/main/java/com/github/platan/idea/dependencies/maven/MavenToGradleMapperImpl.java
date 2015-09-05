package com.github.platan.idea.dependencies.maven;

import com.github.platan.idea.dependencies.gradle.Dependency;
import com.github.platan.idea.dependencies.gradle.Exclusion;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @Override
    @NotNull
    public Dependency map(@NotNull final MavenDependency mavenDependency) {
        List<Exclusion> excludes = Lists.transform(mavenDependency.getExclusions(), MAVEN_EXCLUSION_TO_EXCLUSION_FUNCTION);
        boolean hasWildcardExclude = Iterables.removeIf(mavenDependency.getExclusions(), IS_WILDCARD_EXCLUDE);
        boolean transitive = !hasWildcardExclude;
        return new Dependency(mavenDependency.getGroupId(), mavenDependency.getArtifactId(), mavenDependency.getVersion(),
                Optional.fromNullable(mavenDependency.getClassifier()), getScope(mavenDependency.getScope()), excludes, transitive);
    }

    private String getScope(Scope scope) {
        if (scope == Scope.TEST) {
            return "testCompile";
        } else {
            return scope.toString().toLowerCase();
        }
    }
}
