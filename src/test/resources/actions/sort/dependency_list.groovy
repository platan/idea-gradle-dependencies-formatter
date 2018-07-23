dependencies {
    compile 'group2:name1:1'
    compile 'group5:name1:1'
    compile 'group3:name1:1', 'group2:name2:1'
    compile 'group4:name1:1', 'group3:name2:1'
    compile group: 'group6', name: 'name1', version: '1'
    compile group: 'group1', name: 'name1', version: '1'
}