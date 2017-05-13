package com.github.platan.idea.dependencies.gradle;

import static org.jetbrains.plugins.gradle.util.GradleConstants.SETTINGS_FILE_NAME;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.util.GradleConstants;

public final class GradleFileUtil {

    private static final String DOT_GRADLE = "." + GradleConstants.EXTENSION;

    private GradleFileUtil() {
    }

    public static boolean isGradleFile(@NotNull PsiFile file) {
        return file.getName().endsWith(DOT_GRADLE);
    }

    public static boolean isSettingGradle(@NotNull PsiFile file) {
        return file.getName().equals(SETTINGS_FILE_NAME);
    }
}
