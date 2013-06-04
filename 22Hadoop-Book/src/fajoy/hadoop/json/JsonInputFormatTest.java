package fajoy.hadoop.json;

import java.io.IOException;

import com.alexholmes.json.mapreduce.MultiLineJsonInputFormat;


import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonInputFormatTest extends Configured implements Tool {
  private static final Logger LOG = LoggerFactory.getLogger(JsonInputFormatTest.class);

  private JsonInputFormatTest() {}

  public static class JsonMapper extends Mapper<Text, Text, Text, Text> {
        @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key, value);
    }
  }

  public int run(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: hadoop jar path/to/this.jar " + getClass() + " <input dir> <output dir>");
      System.exit(1);
    }

    Job job = new Job(getConf());
    job.setJobName("JsonInputFormatTest");
    job.setJarByClass(getClass());
    
    job.setInputFormatClass(MultiLineJsonInputFormat.class);
    job.setMapperClass(JsonMapper.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    return job.waitForCompletion(true) ? 0 : 1;
  }

  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new JsonInputFormatTest(), args);
    System.exit(exitCode);
  }
}