package com.github.platan.idea.dependencies;

import com.intellij.codeInsight.editorActions.PasteHandler;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Producer;
import org.jetbrains.annotations.Nullable;

import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import static com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_PASTE;

public class MavenToGradleDependenciesCopyPasteProcessorTest extends LightPlatformCodeInsightFixtureTestCase {

    public void test__convert_maven_to_gradle_while_pasting_to_build_gradle() {
        myFixture.configureByText("build.gradle", "//\n<caret>\n//");
        String toPaste = "<dependency>\n"
                + "\t<groupId>org.spockframework</groupId>\n"
                + "\t<artifactId>spock-core</artifactId>\n"
                + "\t<version>1.0-groovy-2.4</version>\n"
                + "</dependency>";

        runPasteAction(toPaste);

        myFixture.checkResult("//\ncompile 'org.spockframework:spock-core:1.0-groovy-2.4'\n//");
    }

    public void test__convert_maven_to_gradle_while_pasting_to_any_gradle_file() {
        myFixture.configureByText("download.gradle", "<caret>");
        String toPaste = "<dependency>\n"
                + "\t<groupId>org.spockframework</groupId>\n"
                + "\t<artifactId>spock-core</artifactId>\n"
                + "\t<version>1.0-groovy-2.4</version>\n"
                + "</dependency>";

        runPasteAction(toPaste);

        myFixture.checkResult("compile 'org.spockframework:spock-core:1.0-groovy-2.4'");
    }

    public void test__do_not_convert_maven_to_gradle_while_pasting_to_setting_gradle() {
        myFixture.configureByText("settings.gradle", "<caret>");
        String toPaste = "<dependency>\n"
                + "\t<groupId>org.spockframework</groupId>\n"
                + "\t<artifactId>spock-core</artifactId>\n"
                + "\t<version>1.0-groovy-2.4</version>\n"
                + "</dependency>";

        runPasteAction(toPaste);

        myFixture.checkResult("<dependency>\n"
                + "<groupId>org.spockframework</groupId>\n"
                + "\t<artifactId>spock-core</artifactId>\n"
                + "<version>1.0-groovy-2.4</version>\n"
                + "</dependency>");
    }

    private void runPasteAction(final String toPaste) {
        final Producer<Transferable> producer = new Producer<Transferable>() {
            @Nullable
            @Override
            public Transferable produce() {
                return new StringSelection(toPaste);
            }
        };
        EditorActionManager actionManager = EditorActionManager.getInstance();
        EditorActionHandler pasteActionHandler = actionManager.getActionHandler(ACTION_EDITOR_PASTE);
        final PasteHandler pasteHandler = new PasteHandler(pasteActionHandler);
        new WriteCommandAction.Simple(getProject(), new PsiFile[0]) {
            protected void run() throws Throwable {
                Component component = myFixture.getEditor().getComponent();
                DataContext dataContext = DataManager.getInstance().getDataContext(component);
                pasteHandler.execute(myFixture.getEditor(), dataContext, producer);
            }
        }.execute();
    }
}
