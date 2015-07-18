import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PairWiseReducer extends Reducer<IntWritable, Text, Text, DoubleWritable> {

    Text samples = new Text();
    DoubleWritable prod = new DoubleWritable();

    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<ExprSample> list = new ArrayList<ExprSample>();
        for (Text t : values) {
            list.add(new ExprSample(t));
        }

        for (int i = 0; i < list.size() - 1; i++) {
            ExprSample firstSample = list.get(i);

            if (firstSample.getExpr() == 0.0) {
                continue;
            }
            for (int j = i + 1; j < list.size(); j++) {
                ExprSample secondSample = list.get(j);

                if (secondSample.getExpr() == 0.0) {
                    continue;
                }

                StringBuilder b = new StringBuilder();

                if (firstSample.getSample() < secondSample.getSample()) {
                    samples.set(b.append(firstSample.toString()).append(",").append(secondSample.toString()).toString());
                } else if (firstSample.getSample() > secondSample.getSample()) {
                    samples.set(b.append(secondSample.toString()).append(",").append(firstSample.toString()).toString());
                } else {
                    continue;
                }

                prod.set(firstSample.getExpr() * secondSample.getExpr());

                context.write(samples, prod);
            }
        }
    }
    private class ExprSample {

        public double expr;
        public int sample;

        public ExprSample(Text t) {
            String[] parts = t.toString().split(",");
            expr = Double.parseDouble(parts[1]);
            sample = Integer.parseInt(parts[0]);
        }

        public double getExpr() {
            return expr;
        }

        public int getSample() {
            return sample;
        }

        @Override
        public String toString() {
            return "sample_" + sample;
        }
    }
}
