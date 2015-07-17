import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class SampleResult {

    public static class GeneMapper extends Mapper<Object, Text, Text, Text> {
        private Text sampleName = new Text();
        private Text geneNames = new Text();

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer st = new StringTokenizer(value.toString(), ",");
            double maxValue = 0.0;
            Queue<Integer> maxGenes = new LinkedList<Integer>();

            sampleName.set(st.nextToken());
            for (int i = 1; st.hasMoreTokens(); i++) {

                double d = Double.parseDouble(st.nextToken());

                if (d > maxValue) {
                    maxValue = d;
                    maxGenes.clear();
                }

                if (d == maxValue) {
                    maxGenes.add(i);
                }
            }

            StringBuilder sb = new StringBuilder();
            for (int gene : maxGenes) {
                sb.append("gene_").append(gene).append(",");
            }

            if (!maxGenes.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }

            geneNames.set(sb.toString());
            context.write(sampleName, geneNames);
        }
    }
}
