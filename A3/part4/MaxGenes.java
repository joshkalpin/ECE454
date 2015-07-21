import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

import java.io.IOException;
import java.util.List;

public class MaxGenes extends EvalFunc<String> {

    @Override
    public String exec(Tuple input) throws IOException {
        StringBuilder sb = new StringBuilder();
        // this is so unnecessary
        Object geneObject = input.get(0);
        String genes = geneObject.toString().substring(1, geneObject.toString().length() - 1);
        String[] genePool = genes.split(",");

        double max = 0.0;
        for (int i = 0; i < genePool.length; i++) {
            Double geneVal = Double.parseDouble(genePool[i]);
            if (geneVal > max) {
                sb = new StringBuilder();
                max = geneVal;
            }
            if (geneVal.equals(max)) {
                sb.append("gene_").append(i + 1).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}