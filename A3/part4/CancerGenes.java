import org.apache.pig.data.DataType;
import org.apache.pig.EvalFunc;
import org.apache.pig.LoadCaster;
import org.apache.pig.builtin.Utf8StorageConverter;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import java.io.IOException;
import java.lang.System;

public class CancerGenes extends EvalFunc<DataBag> {

    public DataBag exec(Tuple input) throws IOException {
        Object geneObject = input.get(0);
        String genes = geneObject.toString().substring(1, geneObject.toString().length() - 1);
        String[] genePool = genes.split(",");

        DataBag db = BagFactory.getInstance().newDefaultBag();
        for (int i = 0; i < genePool.length; i++) {
            String s = "gene_" + (i + 1);
            Tuple t = TupleFactory.getInstance().newTuple(2);
            t.set(0, s);
            t.set(1, (Double.parseDouble(genePool[i]) > 0.5 ? 1.0 : 0.0));
            db.add(t);
        }

        return db;
    }

    public Schema outputSchema(Schema input) {
        try{
            Schema tupleSchema = new Schema();
            tupleSchema.add(new Schema.FieldSchema("gene_id", DataType.CHARARRAY));
            tupleSchema.add(new Schema.FieldSchema("is_cancerous", DataType.DOUBLE));
            return new Schema(new Schema.FieldSchema("cancer_genes", tupleSchema, DataType.BAG));
        }catch (Exception e){
            return null;
        }
    }
}
