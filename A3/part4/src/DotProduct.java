import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import java.io.IOException;

public class DotProduct extends EvalFunc<Tuple> {

    @Override
    public Tuple exec(Tuple input) throws IOException {
        int firstSample = Integer.parseInt(input.get(0).toString());
        int secondSample = Integer.parseInt(input.get(1).toString());
        String[] firstSampleGenes = input.get(2).toString().split(",");
        String[] secondSampleGenes = input.get(3).toString().split(",");

        Tuple t = TupleFactory.getInstance().newTuple(3);
        t.set(0, "sample_" + firstSample);
        t.set(1, "sample_" + secondSample);

        double sum = 0.0;
        for (int i = 0; i < firstSampleGenes.length; i++) {
            sum += Double.parseDouble(firstSampleGenes[i]) * Double.parseDouble(secondSampleGenes[i]);
        }

        t.set(2, sum);

        return t;
    }

    public Schema outputSchema(Schema input) {
        try{
            Schema tupleSchema = new Schema();
            tupleSchema.add(new Schema.FieldSchema("first_sample", DataType.CHARARRAY));
            tupleSchema.add(new Schema.FieldSchema("second_sample", DataType.CHARARRAY));
            tupleSchema.add(new Schema.FieldSchema("dot_product", DataType.DOUBLE));
            return tupleSchema;
        } catch (Exception e) {
            return null;
        }
    }
}