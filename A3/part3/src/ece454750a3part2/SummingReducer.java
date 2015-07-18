package ece454750a3part2;


import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class SummingReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    DoubleWritable prod = new DoubleWritable();

    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        double sum = 0.0;

        for (DoubleWritable value : values) {
            sum += value.get();
        }

        prod.set(sum);

        context.write(key, prod);
    }
}
