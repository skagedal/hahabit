#!/usr/bin/env bash

# Exit on errors
set -e

if [ -n "$(git status --porcelain)" ]; then
    echo "🚨 Git repo is dirty, aborting"
    exit 1
fi

echo "👋 Building JAR with Java 21..."
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew clean bootJar

echo
echo "👋 Uploading JAR and run script to skagedal.tech..."
scp -i ~/.ssh/hahabit-key build/libs/hahabit-0.0.1-SNAPSHOT.jar hahabit@skagedal.tech: 
scp -i ~/.ssh/hahabit-key server-scripts/run.sh hahabit@skagedal.tech:

echo
echo "👋 Sending SIGTERM to running service..."
ssh -i ~/.ssh/hahabit-key hahabit@skagedal.tech pkill -TERM -f hahabit-0.0.1

echo
echo "👋 Waiting for commit $(git rev-parse --short HEAD) to come up..."
while true; do
    sleep 2
    if [ "$(curl -s https://hahabit.skagedal.tech/actuator/info | jq -r .git.commit.id 2>/dev/null)" = "$(git rev-parse --short HEAD)" ]; then
        echo "Done!"
        exit 0
    else
        echo "Waiting..."
    fi
done


