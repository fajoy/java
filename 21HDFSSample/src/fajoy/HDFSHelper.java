package fajoy;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.*;
import java.util.*;
import java.io.FileNotFoundException;

import java.text.SimpleDateFormat;
public class HFSDHelper {
    FileSystem fs=null;
    Configuration conf = null;
  public static final SimpleDateFormat dateForm = 
    new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Hls(){
    }
    //ses ./src/core/org/apache/hadoop/fs/FsShell.java +570
    public static void main(String[] args) throws Exception {
        HFSDHelper main=new HFSDHelper();
        main.run(args);
        System.exit(0);
    }
    Configuration getConf(){
        return conf;
    }

    void init()throws IOException {
     this.conf = new Configuration();
     this.fs = FileSystem.get(conf);
    }
    public int run(String argv[]) throws Exception {
        //FsShell.main(new String[]{"-ls","./"});
        System.out.println("====test====");
        init();
        ls("./");
        mkdir("./test");
        return 0;
    }

    public void mkdir(String dir) throws IOException{
        Path path = new Path(dir);
        if (fs.exists(path)) {
            System.out.println("Dir " + dir + " already exists!");
            return;
        }
        fs.close();
    }
    public void ls(String srcf) throws IOException{
        boolean recursive=false;

        Path srcPath = new Path(srcf);
        FileSystem srcFs = srcPath.getFileSystem(this.getConf());
        FileStatus[] srcs = srcFs.globStatus(srcPath);
        if (srcs==null || srcs.length==0) {
            throw new FileNotFoundException("Cannot access " + srcf + 
                    ": No such file or directory.");
        }

        boolean printHeader = (srcs.length == 1) ? true: false;
        int numOfErrors = 0;
        for(int i=0; i<srcs.length; i++) {
            numOfErrors += ls(srcs[i], srcFs, recursive, printHeader);
        }

    }

  private int ls(FileStatus src, FileSystem srcFs, boolean recursive,
      boolean printHeader) throws IOException {
    final String cmd = recursive? "lsr": "ls";
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
      
      int maxReplication = 3, maxLen = 10, maxOwner = 0,maxGroup = 0;

      for(int i = 0; i < items.length; i++) {
        FileStatus stat = items[i];
        int replication = String.valueOf(stat.getReplication()).length();
        int len = String.valueOf(stat.getLen()).length();
        int owner = String.valueOf(stat.getOwner()).length();
        int group = String.valueOf(stat.getGroup()).length();
        
        if (replication > maxReplication) maxReplication = replication;
        if (len > maxLen) maxLen = len;
        if (owner > maxOwner)  maxOwner = owner;
        if (group > maxGroup)  maxGroup = group;
      }
      
      for (int i = 0; i < items.length; i++) {
        FileStatus stat = items[i];
        Path cur = stat.getPath();
        String mdate = dateForm.format(new Date(stat.getModificationTime()));
        
        System.out.print((stat.isDir() ? "d" : "-") + 
          stat.getPermission() + " ");
        System.out.printf("%"+ maxReplication + 
          "s ", (!stat.isDir() ? stat.getReplication() : "-"));
        if (maxOwner > 0)
          System.out.printf("%-"+ maxOwner + "s ", stat.getOwner());
        if (maxGroup > 0)
          System.out.printf("%-"+ maxGroup + "s ", stat.getGroup());
        System.out.printf("%"+ maxLen + "d ", stat.getLen());
        System.out.print(mdate + " ");
        System.out.println(cur.toUri().getPath());
        if (recursive && stat.isDir()) {
          numOfErrors += ls(stat,srcFs, recursive, printHeader);
        }
      }
      return numOfErrors;
    }
  }
  private void copyFromStdin(Path dst, FileSystem dstFs) throws IOException {
    if (dstFs.isDirectory(dst)) {
      throw new IOException("When source is stdin, destination must be a file.");
    }
    if (dstFs.exists(dst)) {
      throw new IOException("Target " + dst.toString() + " already exists.");
    }
    FSDataOutputStream out = dstFs.create(dst); 
    try {
      IOUtils.copyBytes(System.in, out, getConf(), false);
    } 
    finally {
      out.close();
    }
  }

/*
  private void createSampleFile() throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();

        FSDataOutputStream out = dstFs.create(dst); 
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("01234567890112345678901234\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

    }
*/
  private static FileStatus[] shellListStatus(String cmd, 
                                                   FileSystem srcFs,
                                                   FileStatus src) {
    if (!src.isDir()) {
      FileStatus[] files = { src };
      return files;
    }
    Path path = src.getPath();
    try {
      FileStatus[] files = srcFs.listStatus(path);
      if ( files == null ) {
        System.err.println(cmd + 
                           ": could not get listing for '" + path + "'");
      }
      return files;
    } catch (IOException e) {
      System.err.println(cmd + 
                         ": could not get get listing for '" + path + "' : " +
                         e.getMessage().split("\n")[0]);
    }
    return null;
  }
 


}
