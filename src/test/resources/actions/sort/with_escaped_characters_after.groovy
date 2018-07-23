dependencies {
    compile group: 'group$', name: 'name', version: '11'
    compile "group\$:name:12"
    compile 'group$:name:13'
    compile group: "group\$", name: 'name', version: '14'
    compile 'group$:name:15' { transitive = false }
    compile "group\$:name:16" { transitive = false }
    compile group: 'group$', name: 'name', version: '17'
    compile "group\$:name:18"
    compile 'group$:name:19'
    compile group: "group\$", name: 'name', version: '20'
    compile 'group$:name:21' { transitive = false }
    compile "group\$:name:22" { transitive = false }
}