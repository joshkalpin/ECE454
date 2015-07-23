/* Part4_1.pig */
register MaxGenes.jar;
sample_data = load '$input' using PigStorage(',');
samples = foreach sample_data generate $0 as sample_id:chararray,  ($1 ..) as genes:tuple();
out = foreach samples generate sample_id, MaxGenes(genes);
store out into '$output' using PigStorage(',');