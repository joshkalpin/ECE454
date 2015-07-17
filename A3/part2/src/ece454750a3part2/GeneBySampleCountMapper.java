package ece454750a3part2;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class GeneBySampleCountMapper extends Mapper<Object, Text, Text, IntWritable>{

    public static enum SampleCounters { SAMPLES }

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

        StringTokenizer st = new StringTokenizer(value.toString(), ",");
        String sampleName = st.nextToken();

        for (int gene = 1; st.hasMoreTokens(); gene++) {
            double d = Double.parseDouble(st.nextToken());
            if (d > 0.5) {
                context.write(new Text("gene_" + gene), new IntWritable(1));
            }
        }

        context.getCounter(SampleCounters.SAMPLES).increment(1);
    }
}
