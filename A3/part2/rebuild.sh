#!/bin/bash

ant
hdfs dfs -rm -r -skipTrash /user/$(whoami)/output
hadoop jar $(pwd)/dist/lib/Part2.jar ece454750a3part2.Part2 /user/$(whoami)/input/1k_Samples/ /user/$(whoami)/output/
echo "Recreating output folder."
rm -rf output/
mkdir output/
echo "Copying output from HDFS."
hdfs dfs -copyToLocal /user/$(whoami)/output/part-r-00000 ./output/
echo "Sorting output and ground truth."
sort output/part-r-00000 > output/part-r-00000.sort
sort input/part2-1k.txt > input/part2-1k.txt.sort
diff --suppress-common-lines --side-by-side --strip-trailing-cr output/part-r-00000.sort input/part2-1k.txt.sort
