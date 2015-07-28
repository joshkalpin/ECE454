import org.apache.pig.EvalFunc;
import org.apache.pig.LoadCaster;
import org.apache.pig.builtin.Utf8StorageConverter;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import java.io.IOException;
import java.lang.String;
import java.lang.StringBuilder;

public class DotProduct extends EvalFunc<String> {
    @Override
    public String exec(Tuple input) throws IOException {
        Tuple inner = (Tuple)input.get(0);

        Tuple firstSampleGenes = (Tuple)inner.get(2);
        Tuple secondSampleGenes = (Tuple)inner.get(3);

        Tuple t = TupleFactory.getInstance().newTuple(3);
        StringBuilder sb = new StringBuilder("sample_");
        sb.append(inner.get(0)).append(",").append("sample_").append(inner.get(1));

        Double sum = 0.0;
        for (int i = 0; i < firstSampleGenes.size(); i++) {
            sum = Double.parseDouble(firstSampleGenes.get(i).toString()) *
                    Double.parseDouble(secondSampleGenes.get(i).toString());
        }

        if (sum == 0.0) {
            return null;
        }

        return sb.append(",").append(sum).toString();
    }

    public Schema outputSchema(Schema input) {
        try{
            Schema tupleSchema = new Schema();
            tupleSchema.add(new Schema.FieldSchema("first_sample", DataType.CHARARRAY));
            tupleSchema.add(new Schema.FieldSchema("second_sample", DataType.CHARARRAY));
            tupleSchema.add(new Schema.FieldSchema("dot_product", DataType.DOUBLE));
            return new Schema(new Schema.FieldSchema("sample_similarity", tupleSchema, DataType.TUPLE));
        } catch (Exception e) {
            return null;
        }
    }
}