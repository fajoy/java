import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.util.StringTokenizer;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HadoopWordCount extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			String errMsg = String.format("Usage: %s <in> <out>",
					HadoopWordCount.class.getSimpleName());
			System.err.println(errMsg);
			System.exit(2);
		}
		int ret = ToolRunner.run(new HadoopWordCount(), otherArgs);
		System.exit(ret);
	}

	@Override
	public int run(String[] args) throws Exception {

		Job job = new Job(getConf());
		job.setJobName("HadoopWordCount");
		job.setJarByClass(HadoopWordCount.class);
		
		job.setMapperClass(StringTokenizerMap.class);
		job.setCombinerClass(CountReduce.class);
		
		job.setReducerClass(CountReduce.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		Path inDir= new Path(args[0]);
		FileInputFormat.addInputPath(job,inDir);
		
		Path outDir= new Path(args[1]);
		FileOutputFormat.setOutputPath(job,outDir);
		
		boolean success=job.waitForCompletion(true) ;
		return success? 0 : 1;
	}
	public void print_result(Path outDir){
		InputStream in=null;
		try {
			String uri= outDir.toString()+"/part-r-00000";
			FileSystem fs= FileSystem.get(URI.create(uri),getConf());
			in=fs.open(new Path(uri));
			IOUtils.copyBytes(in, System.out,4096,false);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeStream(in);
		}
		
	}
	public static class debug {
		public static void log(String msg) {
			
			  InetAddress serverIp; try {
			  
			  serverIp = InetAddress.getByName("140.110.141.130"); int
			  serverPort=5050; DatagramSocket clientSocket = new
			  DatagramSocket(); byte[] sendData = null; sendData =
			  msg.getBytes(); DatagramPacket sendPacket = new
			  DatagramPacket(sendData, sendData.length, serverIp, serverPort);
			  clientSocket.send(sendPacket); clientSocket.close();
			  
			  
			  } catch (IOException e) {}
			 
			// System.out.println(msg);
		}
	}

	public static class StringTokenizerMap extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		public void map(LongWritable key, Text value, Context output)
				throws InterruptedException, IOException {
			
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				word.set(tokenizer.nextToken());
				output.write(word, one);
			}
		}
	}

	public static class CountReduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		
		private IntWritable result = new IntWritable();
		public void reduce(Text key, Iterable<IntWritable> values,
				Context output) throws InterruptedException, IOException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			output.write(key, result);
		}
	}

}
