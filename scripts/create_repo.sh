#!/bin/bash

# For the love of automation
date
echo We are about to start

#Creates a repo directory on the home directory
mkdir -pv "${HOME}"/repo

#Into the directory where the libs/jars are
cd lib

#IntelliJ IDEA Version
IDEA_VERSION="14.1.4"

#Installs to the repo on the home directory
for JAR_FILE in *.jar
do
  echo Installing "${JAR_FILE}" to Maven Repository on "${HOME}"/repo 
  # Reverts the string, removes first five, revert back to normal
  FILE_NAME=$(echo "${JAR_FILE}" |rev |cut -c 5- |rev)
  mvn install:install-file -Dfile="${JAR_FILE}" -DgroupId=com.github.guikeller.jetty-runner -DartifactId="${FILE_NAME}" -Dversion="${IDEA_VERSION}" -Dpackaging=jar -DcreateChecksum=true -DlocalRepositoryPath="${HOME}"/repo
done

echo .....
echo ----- 
echo All done, now drag and drop the repo folder into the \"copy.com folder\" to sync.
echo After the upload process has finished share the \"repo\" directory \( make it public \)
echo Check the \"build.gradle\" to see how use \"copy.com folder\" as a repository
echo -----
date
