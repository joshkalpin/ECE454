import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class NoOpMapper extends Mapper<Object, Text, Text, DoubleWritable> {

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] parts = value.toString().split(",");
        double d;
        if (parts.length < 3) {
            d = 0.0;
        } else {
            d = Double.parseDouble(parts[2]);
        }
        context.write(new Text(parts[0] + "," + parts[1]), new DoubleWritable(d));
    }
}
