#!/usr/bin/env bash
./gradlew jacocoTestReport
echo
awk -F, \
    '{ total += $4 + $5; covered += $5 } END { print "Coverage: " 100*covered/total "%" }' \
     build/reports/jacoco/test/jacocoTestReport.csv
