import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class PairWiseMapper extends Mapper<Object, Text, IntWritable, Text> {
    private IntWritable gene = new IntWritable();
    private Text sampleValue = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

        StringTokenizer st = new StringTokenizer(value.toString(), ",");
        String sampleName = st.nextToken();
        int sampleNum = Integer.parseInt(sampleName.substring(sampleName.indexOf("_") + 1));

        for (int i = 1; st.hasMoreTokens(); i++) {
            String s = st.nextToken();
            if (s.equals("0.0")) {
                continue;
            }
            double expr = Double.parseDouble(s);

            gene.set(i);
            StringBuilder b = new StringBuilder();
            sampleValue.set(b.append(sampleNum).append(',').append(expr).toString());

            context.write(gene, sampleValue);
        }
    }
}
