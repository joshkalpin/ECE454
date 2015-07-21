#!/bin/bash
echo "Don't forget to update your classpath with the pig jar or this won't work"
echo "Building UDF jar"
javac $2.java
jar -cf $2.jar $2*.class
echo "Cleaning up old outputs and log files"
rm -rf output/
mkdir output/
hdfs dfs -rm -r -skipTrash /user/jzanutto/output/
rm *.log
echo "Starting pig script"
pig -param input=/user/jzanutto/input/1k_Samples -param output=/user/jzanutto/output/ $1
echo "Copying output from HDFS."
hdfs dfs -copyToLocal /user/jzanutto/output/
echo "Sorting output."
sort output/part-m-00000
echo "=================== OUTPUT ==================="

cat output/part-m-00000
