package com.github.platan.idea.dependencies.gradle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GradleDependenciesSerializer {

    @NotNull
    String serialize(@NotNull List<Dependency> dependencies);
}
