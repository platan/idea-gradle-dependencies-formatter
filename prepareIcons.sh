#!/bin/sh

SRC=src/main/etc/logo
DEST=src/main/resources/META-INF

cp $SRC/pluginIcon-full.svg $SRC/pluginIcon_dark-full.svg
gsed -i 's/#389FD6/#3592C4/gI' $SRC/pluginIcon_dark-full.svg
gsed -i 's/#6E6E6E/#AFB1B3/gI' $SRC/pluginIcon_dark-full.svg

./node_modules/.bin/svgo $SRC/pluginIcon-full.svg -o $DEST/pluginIcon.svg
./node_modules/.bin/svgo $SRC/pluginIcon_dark-full.svg -o $DEST/pluginIcon_dark.svg
