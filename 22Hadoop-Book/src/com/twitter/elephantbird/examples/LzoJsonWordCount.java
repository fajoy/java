package com.twitter.elephantbird.examples;

import java.io.IOException;
import java.util.Map;

import com.twitter.elephantbird.mapreduce.input.LzoJsonInputFormat;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LzoJsonWordCount extends Configured implements Tool {
  private static final Logger LOG = LoggerFactory.getLogger(LzoJsonWordCount.class);

  private LzoJsonWordCount() {}

  public static class LzoJsonWordCountMapper extends Mapper<LongWritable, MapWritable, Text, LongWritable> {
    private final LongWritable count_ = new LongWritable();
    private final Text word_ = new Text();

    @Override
    protected void map(LongWritable key, MapWritable value, Context context) throws IOException, InterruptedException {
      // Let's assume that our JSON objects consist of key: count pairs, where key is a string and count is an integer.
      for (Map.Entry<Writable, Writable> entry: value.entrySet()) {
        word_.set((Text)entry.getKey());
        Text strValue = (Text)entry.getValue();
        count_.set(Long.parseLong(strValue.toString()));
        context.write(word_, count_);
      }
    }
  }

  public int run(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: hadoop jar path/to/this.jar " + getClass() + " <input dir> <output dir>");
      System.exit(1);
    }

    Job job = new Job(getConf());
    job.setJobName("LZO JSON Word Count");

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(LongWritable.class);

    job.setJarByClass(getClass());
    job.setMapperClass(LzoJsonWordCountMapper.class);
    job.setCombinerClass(LongSumReducer.class);
    job.setReducerClass(LongSumReducer.class);

    // Use the custom LzoTextInputFormat class.
    job.setInputFormatClass(LzoJsonInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    return job.waitForCompletion(true) ? 0 : 1;
  }

  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new LzoJsonWordCount(), args);
    System.exit(exitCode);
  }
}