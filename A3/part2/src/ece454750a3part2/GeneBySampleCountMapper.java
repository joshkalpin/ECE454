package ece454750a3part2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class GeneBySampleCountMapper extends Mapper<Text, Text, Text, IntWritable>{

    public static enum SampleCounters { SAMPLES }

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

        StringTokenizer st = new StringTokenizer(value.toString(), ",");
        String sampleName = st.nextToken();

        for (int gene = 0; st.hasMoreTokens(); gene++) {
            context.write(new Text("gene_" + gene), new IntWritable(Integer.parseInt(st.nextToken())));
        }

        context.getCounter(SampleCounters.SAMPLES).increment(1);
    }
}
