import java.io.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URI;
import java.util.*;

import org.apache.hadoop.mapred.JobClient;
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
import fpTree.*;

public class PFPGrowth extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		// HeadTable h=new HeadTable(new FileInputStream("src/part-r-00000"));
		// h.showTable();
		int ret = ToolRunner.run(new PFPGrowth(), args);
		System.exit(ret);
	}

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = getConf();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 4) {
			String errMsg = String.format("Usage: %s <in> <out> <minSup> <nodeNum>",
					PFPGrowth.class.getSimpleName());
			System.err.println(errMsg);
			System.exit(2);
		}
		int minSup = Integer.parseInt(args[2]);
		int nodeNum = Integer.parseInt(args[3]);
		printNowm(String.format("StartMain min=%d num=%d",minSup,nodeNum));
		
		conf.setInt("minSup", minSup);
		conf.setInt("nodeNum", nodeNum);

		Path inDir = new Path(args[0]);
		Path outDir = new Path(args[1]);
		FileSystem fs = FileSystem.get(getConf());
		fs.mkdirs(outDir);
		Path outDir1 = new Path(args[1] + "/count");
		Path outDir2 = new Path(args[1] + "/fptree");
		Path outDir3 = new Path(args[1] + "/result");
		Boolean success=false;
		printNowm("StartWait1");
		success = freqCount(inDir, outDir1);
		printNowm("EndWait1");
		if (!success)return 1;

		String freqfile = outDir1.toString() + "/part-r-00000";
		HeadTable h = new HeadTable(getBufferedReader(freqfile, conf));
		String orderfile = args[1] + "/order";
		h.outOrder(getBufferedWriter(orderfile, conf), minSup);
		conf.set("orderfile", orderfile);
		printNowm("StartWait2");
		success = createFPtree(inDir, outDir2);
		printNowm("EndWait2");
		if (!success)return 1;
		printNowm("StartWait3");
		success = createCFPtree(outDir2, outDir3);
		printNowm("EndWait3");
		if (!success)return 1;
		printNowm(String.format("End min=%d num=%d",minSup,nodeNum));
		return success ? 0 : 1;
	}

	public static LinkedHashMap<String, Integer> orderMap = null;

	public static LinkedHashMap<String, Integer> getOrderMap(Configuration conf) {
		if (orderMap != null)
			return orderMap;

		orderMap = new LinkedHashMap<String, Integer>();
		String path = conf.get("orderfile");
		BufferedReader reader = getBufferedReader(path, conf);
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				String[] strs = line.split("\t");
				String key = strs[0];
				int value = Integer.valueOf(strs[1]);
				orderMap.put(key, value);
			}
			return orderMap;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return orderMap;

	}

	public boolean freqCount(Path in, Path out) throws IOException,
			InterruptedException, ClassNotFoundException {
		Configuration conf = getConf();
		Job job = new Job(conf);

		job.setJobName(this.getClass().getSimpleName() + "_count");
		job.setJarByClass(PFPGrowth.class);

		job.setMapperClass(SplitMap.class);
		job.setCombinerClass(CountReduce.class);

		job.setReducerClass(FilterCountReduce.class);

		job.setOutputKeyClass(Text.class);

		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, in);
		FileOutputFormat.setOutputPath(job, out);
		job.setNumReduceTasks(1);

		return job.waitForCompletion(true);
	}

	public boolean createFPtree(Path in, Path out) throws IOException,
			InterruptedException, ClassNotFoundException {
		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJobName(this.getClass().getSimpleName() + "_FPtree");
		job.setJarByClass(PFPGrowth.class);

		job.setMapperClass(FPMap.class);
		job.setCombinerClass(CountReduce.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setReducerClass(FPReduce.class);
		int nodeNum = conf.getInt("nodeNum", 0);
		if (nodeNum > 0)
			job.setNumReduceTasks(nodeNum);

		FileInputFormat.addInputPath(job, in);
		FileOutputFormat.setOutputPath(job, out);
		return job.waitForCompletion(true);
	}

	public boolean createCFPtree(Path in, Path out) throws IOException,
			InterruptedException, ClassNotFoundException {
		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJobName(this.getClass().getSimpleName() + "_CFPtree");
		job.setJarByClass(PFPGrowth.class);

		job.setMapperClass(CFPMap.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setReducerClass(CFPReduce.class);
		int nodeNum = conf.getInt("nodeNum", 0);
		if (nodeNum > 0)
			job.setNumReduceTasks(nodeNum);

		FileInputFormat.addInputPath(job, in);
		FileOutputFormat.setOutputPath(job, out);
		return job.waitForCompletion(true);
	}

	public static BufferedWriter getBufferedWriter(String path,
			Configuration conf) {
		BufferedWriter writer = null;
		try {
			FileSystem fs = FileSystem.get(conf);
			OutputStream out = fs.create(new Path(path), true);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			return writer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	}

	public static BufferedReader getBufferedReader(String path,
			Configuration conf) {
		BufferedReader reader = null;
		try {
			FileSystem fs = FileSystem.get(URI.create(path), conf);
			InputStream in = fs.open(new Path(path));
			reader = new BufferedReader(new InputStreamReader(in));
			return reader;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reader;
	}

	/*
	 * public void print_result(Path outDir) { InputStream in = null; try {
	 * String uri = outDir.toString() + "/part-r-00000"; FileSystem fs =
	 * FileSystem.get(URI.create(uri), getConf()); in = fs.open(new Path(uri));
	 * IOUtils.copyBytes(in, System.out, 4096, false); } catch (IOException e) {
	 * e.printStackTrace(); } finally { IOUtils.closeStream(in); }
	 * 
	 * }
	 */
/*
	public static class debug {
		public static void log(String msg) {

			InetAddress serverIp;
			try {
				serverIp = InetAddress.getByName("140.110.141.130");
				int serverPort = 5050;
				DatagramSocket clientSocket = new DatagramSocket();
				byte[] sendData = null;
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, serverIp, serverPort);
				clientSocket.send(sendPacket);
				clientSocket.close();

			} catch (IOException e) {
			}

			// System.out.println(msg);
		}
	}
*/
	/*
	public static void printNow(String msg){
		debug.log(String.format("%s : %d", msg,System.currentTimeMillis()));
	}*/
	public static void printNowm(String msg){
		System.out.println(String.format("%s %s", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), msg));
	}
	public static class SplitMap extends
			Mapper<LongWritable, Text, Text, IntWritable> {
	
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		public void map(LongWritable key, Text value, Context output)
				throws InterruptedException, IOException {

			String line = value.toString();
			try {
				String[] args = line.split("\t", 2);
				String[] items = args[1].split(",");
				for (String item : items) {
					word.set(item);
					output.write(word, one);
				}
			} catch (Exception e) {
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

	public static class FilterCountReduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
	
		private IntWritable result = new IntWritable();
	
		public void reduce(Text key, Iterable<IntWritable> values,
				Context output) throws InterruptedException, IOException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			int minSup = output.getConfiguration().getInt("minSup", 0);
			if (sum < minSup)
				return;
			result.set(sum);
			output.write(key, result);
		}
	}

	public static class CountOrderReduce extends
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

	public static class FPMap extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		private static LinkedHashMap<String, Integer> orderMap = null;

		public void map(LongWritable key, Text value, Context output)
				throws InterruptedException, IOException {
			Configuration conf = output.getConfiguration();

			orderMap = PFPGrowth.getOrderMap(conf);
			String line = value.toString();
			String[] args = line.split("\t", 2);
			String[] items = args[1].split(",");
			ArrayList<String> freqItems = new ArrayList<String>();

			for (String item : items) {
				if (orderMap.containsKey(item))
					freqItems.add(item);
			}
			if (freqItems.size() == 0)
				return;
			Collections.sort(freqItems, sortItemFunc);
			Iterator<String> i = freqItems.iterator();
			StringBuffer str = new StringBuffer();
			str.append(i.next());
			while (i.hasNext()) {
				str.append("," + i.next());
			}
			word.set(str.toString());
			output.write(word, one);
		}

		public static Comparator<String> sortItemFunc = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				// 遞增排序
				return (orderMap.get(o1) > orderMap.get(o2)) ? 1 : -1;
			}
		};
	}

	public static class FPReduce extends Reducer<Text, IntWritable, Text, Text> {

		private Text ok = new Text();
		private Text ov = new Text();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context output) throws InterruptedException, IOException {
			String items = key.toString();
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			ov.set(String.format("%s\t%d", items, sum));
			String[] strs = items.split(",");
			// ok.set(strs[strs.length - 1]);
			ok.set("");
			output.write(ok, ov);
		}
	}

	public static class CFPMap extends Mapper<LongWritable, Text, Text, Text> {
		private Text ov = new Text();
		private Text ok = new Text();
		public void map(LongWritable key, Text value, Context output)
				throws InterruptedException, IOException {
			Configuration conf = output.getConfiguration();
			String line = value.toString();
			String[] args = line.split("\t", 3);
			String[] items = args[1].split(",");
			if (items.length < 2)
				return;
			String sfreq = args[2];
			int freq = Integer.valueOf(sfreq);
			StringBuffer str = new StringBuffer();
			str.append(items[0]);
			for (int i = 1; i < items.length; i++) {
				ok.set(items[i]);
				ov.set(String.format("%s\t%d", str.toString(), freq));
				output.write(ok, ov);
				//debug.log(String.format("cfpmap %s %s",ok.toString(),ov.toString()));
				str.append("," + items[i]);
			}
			
		}
	}

	public static class CFPReduce extends Reducer<Text, Text, Text, Text> {
		private Text ov = new Text();
		private static LinkedHashMap<String, Integer> freqItems = null;
	
		public void reduce(Text key, Iterable<Text> values, Context output)
				throws InterruptedException, IOException {
			freqItems = new LinkedHashMap<String, Integer>();
			for (Text value : values) {
				String line = value.toString();
				String[] args = line.split("\t", 2);
				String[] items = args[0].split(",");
				String sfreq = args[1];
				
				int freq = Integer.valueOf(sfreq);
				for (String item : items) {
					Integer i = freqItems.get(item);
					if (i == null) {
						i = new Integer(0);
					}					
					i += freq;
					freqItems.put(item, i);
				}
			}
	
			StringBuffer str = new StringBuffer();
			List<String> items = new ArrayList<String>(freqItems.keySet());
			Collections.sort(items, sortItemFunc);
			Configuration conf = output.getConfiguration();
			
			String item = items.get(0);
			Integer freq = freqItems.get(item);
			int minSup = conf.getInt("minSup", 0);
			if (freq < minSup)
				return;
			str.append(String.format("%s:%d", items.get(0), freq));
			for (int i = 1; i < items.size(); i++) {
				item = items.get(i);
				freq = freqItems.get(item);
				if (freq < minSup)
					break;
				str.append(String.format(",%s:%d", items.get(i),freqItems.get(items.get(i))));
			}
			ov.set(str.toString());
			output.write(key, ov);
		}
	
		public static Comparator<String> sortItemFunc = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return (freqItems.get(o1) > freqItems.get(o2)) ? -1 : 1;
			}
		};
	
	}
}
