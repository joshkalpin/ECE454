package ece454750a3part2;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class GeneScoreReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {

    private long mapperCounter;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        Cluster cluster = new Cluster(conf);
        Job currentJob = cluster.getJob(context.getJobID());
        mapperCounter = currentJob.getCounters().findCounter(GeneBySampleCountMapper.SampleCounters.SAMPLES).getValue();
    }

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        long sum = 0;

        for (IntWritable i : values) {
            sum += i.get();
        }

        double score = (double)sum/(double)mapperCounter;;
        context.write(new Text(key), new DoubleWritable(score));
    }
}
