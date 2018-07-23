dependencies {
    compile project(':core')
    compile fileTree(dir: 'libs', include: '*.jar')
    compile files(org.gradle.internal.jvm.Jvm.current().getToolsJar())
}