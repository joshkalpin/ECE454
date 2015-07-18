#!/bin/bash

ant
echo "Deleting hdfs output directories."
hdfs dfs -rm -r -skipTrash /user/$(whoami)/output
hdfs dfs -rm -r -skipTrash /user/$(whoami)/temp_output
hadoop jar $(pwd)/Part3.jar Part3 /user/$(whoami)/input/1k_Samples/ /user/$(whoami)/output/
echo "Recreating output folder."
rm -rf output/
mkdir output/
rm -rf temp_output/
mkdir temp_output/
echo "Copying output from HDFS."
hdfs dfs -copyToLocal /user/$(whoami)/output/part-r-00000 ./output/
hdfs dfs -copyToLocal /user/$(whoami)/output/part-r-00000 ./temp_output/
echo "Sorting output and ground truth."
sort output/part-r-00000 > output/part-r-00000.sort
sort input/part3-1k.txt > input/part3-1k.txt.sort
# diff --suppress-common-lines --side-by-side --strip-trailing-cr output/part-r-00000.sort input/part3-1k.txt.sort
