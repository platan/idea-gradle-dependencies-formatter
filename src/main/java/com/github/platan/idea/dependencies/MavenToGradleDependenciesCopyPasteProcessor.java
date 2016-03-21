package com.github.platan.idea.dependencies;

import static com.github.platan.idea.dependencies.gradle.GradleFileUtil.isGradleFile;
import static com.github.platan.idea.dependencies.gradle.GradleFileUtil.isSettingGradle;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MavenToGradleDependenciesCopyPasteProcessor implements CopyPastePreProcessor {

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
        return isGradleFile(file) && !isSettingGradle(file);
    }

    @NotNull
    private String preprocessedText(@NotNull String text) {
        return mavenToGradleConverter.convert(text);
    }

}
