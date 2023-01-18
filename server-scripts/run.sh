#!/usr/bin/env bash
export SPRING_DATASOURCE_USERNAME=hahabit
export SPRING_DATASOURCE_PASSWORD=$(cat postgres-password)
export LOGGING_FILE_PATH=/home/hahabit 
java -jar hahabit-0.0.1-SNAPSHOT.jar
