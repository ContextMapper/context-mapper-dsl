#!/bin/sh -e

# checkout master
git checkout -qf master;

# perform release
./mvnw --settings ./.travis/settings.xml clean install release:prepare release:perform -B

