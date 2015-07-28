register UDF.jar;
sample_data = load '$input' using PigStorage(',');
samples = foreach sample_data generate STRSPLIT((chararray)$0, '_').$1 as sample_id:int, ($1 ..) as genes:tuple();
samples_copy = foreach sample_data generate STRSPLIT((chararray)$0, '_').$1 as sample_id:int, ($1 ..) as genes:tuple();
proper_pairs = CROSS samples, samples_copy;
unduped_pairs = FILTER proper_pairs BY samples::sample_id < samples_copy::sample_id;
out = foreach unduped_pairs generate
    DotProduct(TOTUPLE(samples::sample_id, samples_copy::sample_id, samples::genes, samples_copy::genes));
store out into '$output' using PigStorage(',');