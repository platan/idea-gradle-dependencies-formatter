dependencies {
    compile 'org.slf4j:slf4j-api:1.7.10'
    compile(group: 'junit', name: 'junit', version: '4.11')
            {
                transitive = false
            }
    compile('com.google.truth:truth:0.27') {
        // disable all transitive dependencies of this dependency
        transitive = false
    }
    compile 'com.google.guava:guava:17.0' { transitive = false }
}