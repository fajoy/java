package fajoy.hadoop.json;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.*;


import com.manning.hip.ch3.json.JsonInputFormat;

import java.io.IOException;

public final class JsonMapReduce {
  private static final String JSON_KEY_NAME="JSON_KEY_NAME";
  private static final String JSON_VALUE_NAME="JSON_VALUE_NAME";

  public static class Map extends Mapper<LongWritable, MapWritable,
      Text, Text> {

    @Override
    protected void map(LongWritable key, MapWritable value,
                       Context context)
        throws
        IOException, InterruptedException {
      Text outkey=null;
      Text outvalue=null;
      String key_name=context.getConfiguration().get(JSON_KEY_NAME);
      String value_name=context.getConfiguration().get(JSON_VALUE_NAME);
      for (java.util.Map.Entry<Writable, Writable> entry : value.entrySet()) {
    	  if(entry.getKey().toString().equals(key_name))
    			  outkey=(Text)entry.getValue();
    	  if(entry.getKey().toString().equals(value_name))
    		  outvalue=(Text)entry.getValue();  
      }
      if (outkey!=null &&outvalue!=null)
    	  context.write(outkey	,outvalue);
    }
  }

  public static void main(String... args) throws Exception {
    runJob(args[0], args[1],args[2],args[3]);
  }

  public static void runJob(String input,
                            String output,String key_name,String value_name)
      throws Exception {
    Configuration conf = new Configuration();
    conf.set(JSON_KEY_NAME, key_name);
    conf.set(JSON_VALUE_NAME, value_name);
    Job job = new Job(conf);
    job.setJarByClass(JsonMapReduce.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setMapperClass(Map.class);
    job.setInputFormatClass(JsonInputFormat.class);
    job.setNumReduceTasks(0);
    job.setOutputFormatClass(TextOutputFormat.class);

    FileInputFormat.setInputPaths(job, new Path(input));
    Path outPath = new Path(output);
    FileOutputFormat.setOutputPath(job, outPath);
    outPath.getFileSystem(conf).delete(outPath, true);

    job.waitForCompletion(true);
  }
}
