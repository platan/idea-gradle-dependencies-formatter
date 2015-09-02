package com.github.platan.idea.dependencies;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.util.GradleConstants;

public class MavenToGradleDependenciesCopyPasteProcessor implements CopyPastePreProcessor {

    private static final String DOT_GRADLE = "." + GradleConstants.EXTENSION;
    private final MavenToGradleConverter mavenToGradleConverter;

    public MavenToGradleDependenciesCopyPasteProcessor(MavenToGradleConverter mavenToGradleConverter) {
        this.mavenToGradleConverter = mavenToGradleConverter;
    }

    @Nullable
    @Override
    public String preprocessOnCopy(PsiFile file, int[] startOffsets, int[] endOffsets, String text) {
        return null;
    }

    @NotNull
    @Override
    public String preprocessOnPaste(Project project, PsiFile file, Editor editor, String text, RawText rawText) {
        if (!canPasteForFile(file)) {
            return text;
        }
        return preprocessedText(text);
    }

    private boolean canPasteForFile(@NotNull PsiFile file) {
        return isGradleFile(file);
    }

    private boolean isGradleFile(@NotNull PsiFile file) {
        return file.getName().endsWith(DOT_GRADLE) && !file.getName().equals(GradleConstants.SETTINGS_FILE_NAME);
    }

    @NotNull
    private String preprocessedText(@NotNull String text) {
        return mavenToGradleConverter.convert(text);
    }

}
