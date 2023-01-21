#!/usr/bin/env bash

# Exit on errors
set -e

echo 👋 Building JAR with Java 19...
export JAVA_HOME=$(/usr/libexec/java_home -v 19)
./gradlew clean bootJar

echo
echo 👋 Uploading JAR and run script to skagedal.tech...
scp -i ~/.ssh/hahabit-key build/libs/hahabit-0.0.1-SNAPSHOT.jar hahabit@skagedal.tech: 
scp -i ~/.ssh/hahabit-key server-scripts/run.sh hahabit@skagedal.tech:

echo
echo 👋 Sending SIGTERM to running service...
ssh -i ~/.ssh/hahabit-key hahabit@skagedal.tech pkill -TERM -f hahabit-0.0.1
