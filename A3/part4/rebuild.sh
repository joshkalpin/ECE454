#!/bin/bash
echo "Don't forget to update your classpath with the pig jar or this won't work"
echo "Building UDF jar"
ant
echo "Cleaning up old outputs and log files"
rm -rf output/
mkdir output/
hdfs dfs -rm -r -skipTrash /user/$(whoami)/output/
rm *.log
echo "Starting pig script"
pig -param input=/user/$(whoami)/input -param output=/user/$(whoami)/output/ $1.pig
echo "Copying output from HDFS."
hdfs dfs -copyToLocal /user/$(whoami)/output/
echo "Sorting output."
sort output/part-*-00000 > output/part-*-00000.sort
echo "=================== OUTPUT ==================="
# cat output/part-*-00000