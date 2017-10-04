#!/bin/bash

# For the love of automation
date
echo We are about to start

#properties
IDEA_VERSION="2017.1"
INTELLIJ_HOME="/cygdrive/c/Program Files (x86)/JetBrains/IntelliJ IDEA "$IDEA_VERSION
INTELLIJ_LIB=$INTELLIJ_HOME/lib

# Into the directory where the libs/jars of IntelliJ are on Windows
mkdir -pv /cygdrive/c/tmp/IJ
cp -Rf "/cygdrive/c/Program Files (x86)/JetBrains/IntelliJ IDEA 2017.1/lib" /cygdrive/c/tmp/IJ
cd /cygdrive/c/tmp/IJ/lib

# Installs to the repo on the home directory
for JAR_FILE in annotations.jar extensions.jar idea.jar idea_rt.jar jdom.jar openapi.jar trove4j.jar util.jar
do
  echo Installing "${JAR_FILE}" to Repository
  # Reverts the string, removes first five, revert back to normal
  FILE_NAME=$(echo "${JAR_FILE}" |rev |cut -c 5- |rev)
  mvn install:install-file -Dfile="${JAR_FILE}" -DgroupId=com.github.guikeller.jetty-runner -DartifactId="${FILE_NAME}" -Dversion="${IDEA_VERSION}" -Dpackaging=jar -DcreateChecksum=true
done

echo .....
echo ----- 
echo All done, IntelliJ jars have been installed into your local maven repo.
echo -----
date
