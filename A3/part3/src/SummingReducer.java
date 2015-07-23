import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class SummingReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
    DoubleWritable prod = new DoubleWritable();

    @Override
    public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        double sum = 0.0;

        for (DoubleWritable value : values) {
            sum += value.get();
        }

        sum = Math.floor(sum * 100) / 100;
        prod.set(sum);
        String[] keySamples = key.toString().split(",");
        context.write(new Text("sample_" + keySamples[0] + ",sample_" + keySamples[1]), prod);
    }
}
