package ece454750a3part2;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class NoOpMapper extends Mapper<Object, DoubleWritable, Text, DoubleWritable>{

    @Override
    public void map(Object key, DoubleWritable value, Context context) throws IOException, InterruptedException {
        context.write(new Text(key.toString()), value);
    }
}
