#!/usr/bin/env sh
java -client -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xverify:none -jar ${project.artifactId}.jar $@
