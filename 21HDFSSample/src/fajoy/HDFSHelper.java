package fajoy;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.fs.*;
import java.util.*;
import java.io.FileNotFoundException;

import java.text.SimpleDateFormat;

public class HDFSHelper {
	public static final SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public HDFSHelper(){

	}
	public Configuration getConf() {
		return new Configuration();
	}
	public static void main(String[] args) throws Exception {
		HDFSHelper fs=new HDFSHelper();
		fs.copyFromStdin(System.in, "./tmpaa");
		fs.mkdir("./aaaaaa");
		fs.ls("./", true);
		fs.cat("./tmpaa", true);
		System.exit(0);
	}
	
	public void mkdir(String dir) throws IOException {
		FileSystem fs=FileSystem.get(getConf());
		Path path = new Path(dir);
		if (fs.exists(path)) {
			System.out.println("Dir " + dir + " already exists!");
			return;
		}
		fs.close();
	}

	public void ls(String srcf,boolean recursive) throws IOException {
		Path srcPath = new Path(srcf);
		FileSystem srcFs = srcPath.getFileSystem(this.getConf());
		FileStatus[] srcs = srcFs.globStatus(srcPath);
		if (srcs == null || srcs.length == 0) {
			throw new FileNotFoundException("Cannot access " + srcf	+ ": No such file or directory.");
		}

		boolean printHeader = (srcs.length == 1) ? true : false;
		int numOfErrors = 0;
		for (int i = 0; i < srcs.length; i++) {
			numOfErrors += ls(srcs[i], srcFs, recursive, printHeader);
		}
	}

	private int ls(FileStatus src, FileSystem srcFs, boolean recursive,
			boolean printHeader) throws IOException {
		final String cmd = recursive ? "lsr" : "ls";
		final FileStatus[] items = shellListStatus(cmd, srcFs, src);
		if (items == null) {
			return 1;
		} else {
			int numOfErrors = 0;
			if (!recursive && printHeader) {
				if (items.length != 0) {
					System.out.println("Found " + items.length + " items");
				}
			}

			int maxReplication = 3, maxLen = 10, maxOwner = 0, maxGroup = 0;

			for (int i = 0; i < items.length; i++) {
				FileStatus stat = items[i];
				int replication = String.valueOf(stat.getReplication())
						.length();
				int len = String.valueOf(stat.getLen()).length();
				int owner = String.valueOf(stat.getOwner()).length();
				int group = String.valueOf(stat.getGroup()).length();

				if (replication > maxReplication)
					maxReplication = replication;
				if (len > maxLen)
					maxLen = len;
				if (owner > maxOwner)
					maxOwner = owner;
				if (group > maxGroup)
					maxGroup = group;
			}

			for (int i = 0; i < items.length; i++) {
				FileStatus stat = items[i];
				Path cur = stat.getPath();
				String mdate = dateForm.format(new Date(stat.getModificationTime()));

				System.out.print((stat.isDir() ? "d" : "-")	+ stat.getPermission() + " ");
				System.out.printf("%" + maxReplication + "s ",
						(!stat.isDir() ? stat.getReplication() : "-"));
				if (maxOwner > 0)
					System.out.printf("%-" + maxOwner + "s ", stat.getOwner());
				if (maxGroup > 0)
					System.out.printf("%-" + maxGroup + "s ", stat.getGroup());
				System.out.printf("%" + maxLen + "d ", stat.getLen());
				System.out.print(mdate + " ");
				System.out.println(cur.toUri().getPath());
				if (recursive && stat.isDir()) {
					numOfErrors += ls(stat, srcFs, recursive, printHeader);
				}
			}
			return numOfErrors;
		}
	}

	
	public   void cat(final String src, boolean verifyChecksum) throws IOException {
	    //cat behavior in Linux
	    //  [~/1207]$ ls ?.txt
	    //  x.txt  z.txt
	    //  [~/1207]$ cat x.txt y.txt z.txt
	    //  xxx
	    //  cat: y.txt: No such file or directory
	    //  zzz

	    Path srcPattern = new Path(src);
	    new DelayedExceptionThrowing() {
	      @Override
	      void process(Path p, FileSystem srcFs) throws IOException {
	        printToStdout(srcFs.open(p));
	      }
	    }.globAndProcess(srcPattern, getSrcFileSystem(srcPattern, verifyChecksum));
	  }
	

	  /**
	   * Print from src to stdout.
	   */
	  private void printToStdout(InputStream in) throws IOException {
	    try {
	      IOUtils.copyBytes(in, System.out, getConf(), false);
	    } finally {
	      in.close();
	    }
	  }



	private void copyFromStdin(InputStream in, String path) throws IOException {
		FileSystem fs=FileSystem.get(getConf());
		Path dst=new Path(path);
		if (fs.isDirectory(dst)) {
			throw new IOException("When source is stdin, destination must be a file.");
		}
		if (fs.exists(dst)) {
			throw new IOException("Target " + dst.toString()+ " already exists.");
		}
		FSDataOutputStream out = fs.create(dst);
		try {
			IOUtils.copyBytes(in, out, getConf(), false);
		} finally {
			out.close();
		}
	}

	/*
	 * private void createSampleFile() throws IOException { File file =
	 * File.createTempFile("aws-java-sdk-", ".txt"); file.deleteOnExit();
	 * 
	 * FSDataOutputStream out = dstFs.create(dst); Writer writer = new
	 * OutputStreamWriter(new FileOutputStream(file));
	 * writer.write("abcdefghijklmnopqrstuvwxyz\n");
	 * writer.write("01234567890112345678901234\n");
	 * writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
	 * writer.write("01234567890112345678901234\n");
	 * writer.write("abcdefghijklmnopqrstuvwxyz\n"); writer.close();
	 * 
	 * }
	 */
	private static FileStatus[] shellListStatus(String cmd, FileSystem srcFs,FileStatus src) {
		if (!src.isDir()) {
			FileStatus[] files = { src };
			return files;
		}
		Path path = src.getPath();
		try {
			FileStatus[] files = srcFs.listStatus(path);
			if (files == null) {System.err.println(cmd + ": could not get listing for '" + path	+ "'");
			}
			return files;
		} catch (IOException e) {
			System.err.println(cmd + ": could not get get listing for '" + path	+ "' : " + e.getMessage().split("\n")[0]);
		}
		return null;
	}
	
	  /**
	   * Return the {@link FileSystem} specified by src and the conf.
	   * It the {@link FileSystem} supports checksum, set verifyChecksum.
	   */
	  private FileSystem getSrcFileSystem(Path src, boolean verifyChecksum) throws IOException {
	    FileSystem srcFs = src.getFileSystem(getConf());
	    srcFs.setVerifyChecksum(verifyChecksum);
	    return srcFs;
	  }
	  
	
	  /**
	   * Accumulate exceptions if there is any.  Throw them at last.
	   */
	  private abstract class DelayedExceptionThrowing {
	    abstract void process(Path p, FileSystem srcFs) throws IOException;

	    final void globAndProcess(Path srcPattern, FileSystem srcFs
	        ) throws IOException {
	      List<IOException> exceptions = new ArrayList<IOException>();
	      for(Path p : FileUtil.stat2Paths(srcFs.globStatus(srcPattern),
	                                       srcPattern))
	        try { process(p, srcFs); }
	        catch(IOException ioe) { exceptions.add(ioe); }

	      if (!exceptions.isEmpty())
	        if (exceptions.size() == 1)
	          throw exceptions.get(0);
	        else
	          throw new IOException("Multiple IOExceptions: " + exceptions);
	    }
	  }

}
