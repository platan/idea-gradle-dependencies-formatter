# Gradle dependencies formatter [![Build Status](https://travis-ci.org/platan/idea-gradle-dependencies-formatter.svg?branch=master)](https://travis-ci.org/platan/idea-gradle-dependencies-formatter)
IntelliJ IDEA plugin for formatting Gradle dependencies. 

## Features

- Paste a Maven dependency as a Gradle dependency

## Installation

- Build using `./gradlew build` and install from file `build/libs/idea-gradle-dependencies-formatter-x.x.x.jar`:  
`File` > `Settings` > `Plugins` > `Install plugin from disk...` > Choose plugin file in `Choose Plugin File` window > `OK`

## Usage

### Paste a Maven dependency as a Gradle dependency

1. Copy a declaration of a Maven dependency in any editor.
2. Paste it into gradle file to dependencies section. Plugin will automatically convert it to a Gradle dependency.

If you need to paste XML with maven dependency into gradle file without modification, please use `Paste Simple` action.

## Development

1. Download and extract IntelliJ IDEA distribution:  
`./gradlew -b downloadIdea.gradle extractIdeaSdk`

1. Build:  
`./gradlew build`

In order to build plugin against specific IntelliJ IDEA version (e.g. 13.1.6) use this commands:
`./gradlew -b downloadIdea.gradle -P ideaVersion=13.1.6 extractIdeaSdk`  
`./gradlew -PideaVersion=13.1.6 build`

Note: Currently the project is not prepared to be imported as a IntelliJ Platform Plugin.

## Changelog

2015-09-02 - Initial release

## License

This project is licensed under the MIT license.
