package com.github.platan.idea.dependencies

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import spock.lang.Specification

class MavenToGradleDependenciesCopyPasteProcessorSpec extends Specification {
    
    def "do not convert maven to gradle while pasting to setting gradle"(){
        given:
        def mavenToGradleConverter = Mock(MavenToGradleConverter)
        MavenToGradleDependenciesCopyPasteProcessor copyPasteProcessor = new MavenToGradleDependenciesCopyPasteProcessor(mavenToGradleConverter)
        PsiFile file = Stub(PsiFile) {
            getName() >> 'settings.gradle'
        }
        when:
        copyPasteProcessor.preprocessOnPaste(Stub(Project), file, Stub(Editor), 'text', new RawText('text'))

        then:
        0 * mavenToGradleConverter.convert(_)
    }
}
