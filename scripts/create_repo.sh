#!/bin/bash

# For the love of automation
date
echo We are about to start

# Into the directory where the libs/jars of IntelliJ are on MacOS
mkdir -pv /tmp/IJ
cp -Rf /Applications/IntelliJ\ IDEA.app/Contents/lib /tmp/IJ
cd /tmp/IJ/lib

# IntelliJ IDEA Version
IDEA_VERSION="14.1.4"

# Installs to the repo on the home directory
for JAR_FILE in *.jar
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
