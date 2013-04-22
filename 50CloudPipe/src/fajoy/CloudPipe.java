package fajoy;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.fs.*;
import java.util.*;
import java.io.FileNotFoundException;

import java.text.SimpleDateFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.UUID;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketWebsiteConfigurationRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.StringUtils;
import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class CloudPipe {
	public static final SimpleDateFormat dateForm = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final int BuffSize=4096; 
	public CloudPipe() {

	}

	public static void main(String[] args) throws Exception {
		
        String accessKey = "";
        String secretKey = "";
        String endpoint="";
        
        String configFile = System.getProperties().getProperty("user.dir")+"/.s3";
        System.out.print(configFile);
        Properties properties=getProperties(getInputStreamFormLocal(configFile));
        

        accessKey=properties.getProperty("accessKey");
        secretKey=properties.getProperty("secretKey");
        endpoint=properties.getProperty("endpoint");

        System.out.println(accessKey);
        System.out.println(secretKey);
        System.out.println(endpoint);
        
        //copyToHDFS(getInputStreamFormLocal(configFile), "./.s3",false);
        AmazonS3 s3=getS3(accessKey, secretKey, endpoint);
        
        String bucketName="hadoop";
        ObjectListing objectListing = s3.listObjects(bucketName);
        for (S3ObjectSummary obj : objectListing.getObjectSummaries()) {
            System.out.println(" - " + obj.getKey() + "  " + "(size = " + obj.getSize() + ")");
            InputStream s3in= getInputStreamFormS3(s3, bucketName, obj.getKey());
            if(obj.getKey().endsWith("/"))
            	mkdirHDFS("./"+obj.getKey());
            else
            	copyToHDFS(s3in, obj.getKey(), false);
        }
        
        Date now = new Date();
        String nowStr=dateForm.format(now);
        
        String newbucketName="out"+nowStr;
        s3.createBucket(newbucketName);
        System.out.println("create bucket:"+newbucketName);
        Path srcPath=new Path ("./"); 
        FileSystem srcFs = srcPath.getFileSystem(new Configuration());
		FileStatus[] srcs = srcFs.globStatus(srcPath);
		
		for (FileStatus  fss :srcs){
	
			if(fss.isDir()){
				System.out.println("dir:"+fss.getPath());			
			}else{
				System.out.println("file:"+fss.getPath()+" size:"+fss.getLen());
				InputStream hdfsin =getInputStreamFormHDFS(fss.getPath().getName());
				copyToS3(hdfsin,s3, newbucketName, fss.getPath().getName());
			}		
		}
		
		System.exit(0);
	}
	
	
	
	public static InputStream getInputStreamFormLocal(String path) throws IOException {
		return new FileInputStream(path);
	}
	
	public static InputStream getInputStreamFormHDFS(String path) throws IOException {
		FileSystem fs = FileSystem.get(new Configuration());
		return fs.open(new Path(path));
	}
	public static InputStream getInputStreamFormS3(AmazonS3 s3,String bucketName,String key) throws IOException {
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
		return object.getObjectContent();
	}
	
	
	public static void copyToLocal(InputStream in,String path,boolean append) throws IOException {
		FileOutputStream out =new FileOutputStream(path,append);
		try {
			IOUtils.copyBytes(in, out, BuffSize);
		} finally {
			out.close();
		}
	}
	
	public static void copyToHDFS(InputStream in,String path,boolean append) throws IOException {
		FileSystem fs = FileSystem.get(new Configuration());
		OutputStream out =(append)?fs.append(new Path(path)):fs.create(new Path(path));
		
		try {
			IOUtils.copyBytes(in, out, BuffSize);
		} finally {
			out.close();
		}
		
	}
	public static void copyToS3(InputStream input,AmazonS3 s3,String bucketName,String key) throws IOException {
		ObjectMetadata metadata=new ObjectMetadata();
		PutObjectRequest req=new PutObjectRequest(bucketName, key, input, metadata);
		s3.putObject(req);		
	}
	
	public static void mkdirHDFS(String dir) throws IOException {
		FileSystem fs = FileSystem.get(new Configuration() );
		Path path = new Path(dir);
		if (fs.exists(path)) {
			System.err.println("Dir " + dir + " already exists!");
			return;
		}
		fs.close();
	}
	
	public static Properties getProperties(InputStream in) {
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return properties;
	}
	public static AmazonS3 getS3 (String accessKey,String secretKey,String endpoint){
		 	AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	     	AmazonS3 s3 = new AmazonS3Client(credentials);
	        s3.setEndpoint(endpoint);
	        S3ClientOptions clientOptions =new S3ClientOptions();
	        clientOptions.setPathStyleAccess(true);
	        s3.setS3ClientOptions(clientOptions );
	        return s3;
	}


	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

}
