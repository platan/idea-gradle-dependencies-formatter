# Gradle dependencies formatter [![Build Status](https://travis-ci.org/platan/idea-gradle-dependencies-formatter.svg?branch=master)](https://travis-ci.org/platan/idea-gradle-dependencies-formatter) [![Coverage Status](https://coveralls.io/repos/platan/idea-gradle-dependencies-formatter/badge.svg?branch=master&service=github)](https://coveralls.io/github/platan/idea-gradle-dependencies-formatter?branch=master)
IntelliJ IDEA plugin for formatting Gradle dependencies. 

## Features

- Convert a string notation to a map notation
- Convert a map notation to a string notation
- Sort dependencies
- Paste a Maven dependency as a Gradle dependency (on the fly conversion)

## Installation

Install using the JetBrains Plugin Repository:  
`File` > `Settings` > `Plugins` > `Browse repositories...` > type `gradle dependencies formatter` in search form > `Install plugin`

## Usage

### Convert between a string notation and a map notation

Use `Show Intention Actions` action (`Alt + Enter` or ⌥⏎) and choose `Convert to map notation` or `Convert to string notation`.

![Convert a string notation to a map notation](https://raw.githubusercontent.com/platan/idea-gradle-dependencies-formatter/master/readme/convert.gif)

### Sort dependencies

In order to sort dependencies open a `.gradle` file and use `Sort Gradle dependencies` action from `Code` menu. 

![Sort dependencies](https://raw.githubusercontent.com/platan/idea-gradle-dependencies-formatter/master/readme/sort.gif)

### Paste a Maven dependency as a Gradle dependency

1. Copy a declaration of a Maven dependency in any editor.
2. Paste it into gradle file to dependencies section. Plugin will automatically convert it to a Gradle dependency.

![Paste a Maven dependency as a Gradle dependency](https://raw.githubusercontent.com/platan/idea-gradle-dependencies-formatter/master/readme/paste.gif)

Optional dependencies are coded using syntax defined by [Nebula Extra Configurations](https://github.com/nebula-plugins/gradle-extra-configurations-plugin/). 

If you need to paste XML with maven dependency into gradle file without modification, please use `Paste Simple` action.

Note: This feature was added to IntelliJ IDEA in version 2016.3. It converts a single maven dependency at once and handles only simple cases. 

## Development

Build:  
`./gradlew build`

In order to build plugin against specific IntelliJ IDEA version (e.g. 13.1.6) use this commands:  
`./gradlew -P ideaVersion=13.1.6 build`

Note: Currently the project is not prepared to be imported as a IntelliJ Platform Plugin.

## Changelog

### 0.5.0 (2016-03-29)
- (feature) Sort dependencies

### 0.4.0 (2015-10-20)
- (feature) Convert a map notation to a string notation
- (bugfix) Convert string notation of a dependency with ext to a map notation

### 0.3.0 (2015-09-22)
- Convert a string notation to a map notation

### 0.2.0 (2015-09-08)
- Paste a Maven dependency as a Gradle dependency:
    - added support for elements `classifier` and `optional`
    - version is skipped for dependency without version

### 0.1.0 - initial release (2015-09-02)
- Paste a Maven dependency as a Gradle dependency

## License

This project is licensed under the MIT license.
