package ece454750a3part2;


import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class PairWiseReducer extends Reducer<IntWritable, Text, Text, DoubleWritable> {

    Text samples = new Text();
    DoubleWritable prod = new DoubleWritable();

    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int i = 0;
        for (Text value : values) {
            String[] parts = value.toString().split(",");
            double expr = Double.parseDouble(parts[2]);
            int sample = Integer.parseInt(parts[1]);
            i++;
            Iterator<Text> iterator = values.iterator();

            for (int j = 0; j < i; j++) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Text otherValue = iterator.next();
                String[] otherParts = otherValue.toString().split(",");
                double expr2 = Double.parseDouble(otherParts[2]);
                int sample2 = Integer.parseInt(otherParts[1]);

                StringBuilder b = new StringBuilder();

                if (sample < sample2) {
                    samples.set(b.append(sample).append(':').append(sample2).toString());
                } else if (sample > sample2) {
                    samples.set(b.append(sample2).append(':').append(sample).toString());
                } else {
                    continue;
                }

                prod.set(expr * expr2);

                context.write(samples, prod);
            }
        }
    }
}
