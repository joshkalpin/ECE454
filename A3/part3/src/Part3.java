import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class Part3 extends Configured implements Tool {

    public static final String OUTPUT_DIR = "temp_output";

    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        conf.set("mapreduce.output.textoutputformat.separator", ",");
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if (otherArgs.length != 2) {
            System.err.println("Usage: Part3 <in> <out>");
            System.exit(2);
        }

        Job job1 = Job.getInstance(conf, "Part3-1");
        // performance gains - probably won't be used though
        job1.setSpeculativeExecution(true);

        job1.setMapperClass(PairWiseMapper.class);
        job1.setReducerClass(PairWiseReducer.class);

        job1.setMapOutputKeyClass(IntWritable.class);
        job1.setMapOutputValueClass(Text.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job1, new Path(OUTPUT_DIR));

        job1.setJar("Part3.jar");
        job1.setJarByClass(Part3.class);
        job1.waitForCompletion(true);

        Configuration conf2 = getConf();
        conf2.set("mapreduce.output.textoutputformat.separator", ",");
        Job job2 = Job.getInstance(conf2, "Part3-2");

        job2.setMapperClass(NoOpMapper.class);
        job2.setReducerClass(SummingReducer.class);

        job2.setMapOutputKeyClass(Text.class);
        job2.setMapOutputValueClass(DoubleWritable.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job2, new Path(OUTPUT_DIR));
        FileOutputFormat.setOutputPath(job2, new Path(otherArgs[1]));

        job2.setJar("Part3.jar");
        job2.setJarByClass(Part3.class);
        return job2.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new Part3(), args);
    }
}
