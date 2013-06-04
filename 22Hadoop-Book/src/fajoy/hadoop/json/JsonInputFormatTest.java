package fajoy.hadoop.json;

import java.io.IOException;

import com.alexholmes.json.mapreduce.MultiLineJsonInputFormat;
import com.manning.hip.ch3.json.JsonInputFormat;
import com.manning.hip.ch3.json.JsonMapReduce;
import com.manning.hip.ch3.json.JsonMapReduce.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonInputFormatTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(JsonInputFormatTest.class);

	private JsonInputFormatTest() {
	}

	public static class JsonMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
		Text newValue=new Text();;
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			newValue.set(value.toString().replace("\n", ""));
			context.write(key, newValue);
		}
	}

	public static void main(String... args) throws Exception {
		runJob(args[0], args[1]);
	}

	public static void runJob(String input, String output) throws Exception {
		Configuration conf = new Configuration();
		conf.set("multilinejsoninputformat.member", "address_components");
		Job job = new Job(conf);

		job.setJobName("JsonInputFormatTest");
		job.setJarByClass(JsonInputFormatTest.class);

		job.setInputFormatClass(MultiLineJsonInputFormat.class);
		job.setMapperClass(JsonMapper.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.setInputPaths(job, new Path(input));
		Path outPath = new Path(output);
		FileOutputFormat.setOutputPath(job, outPath);
		outPath.getFileSystem(conf).delete(outPath, true);

		job.waitForCompletion(true);
	}

}