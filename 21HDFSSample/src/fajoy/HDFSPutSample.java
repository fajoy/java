package fajoy;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;


public class HDFSPutSample {
	public static final SimpleDateFormat dateForm = new SimpleDateFormat("yyyyMMddHHmmss");

	public static void main(String[] args) throws IOException {


		String src = "src";
		String dst = "test"+dateForm.format(new Date());
		File dir = new File(src);
		
		final FileSystem fs = FileSystem.get(new Configuration());
		FileAction put = new FileAction() {
			String fileSeparator = System.getProperty("file.separator");
			@Override
			public void actionFile(File file) {
				String path = file.getPath();
				if (!fileSeparator.equals("/"))
					path = path.replace(fileSeparator, "/");
				
				try {
					InputStream is = new FileInputStream(file.getPath());
					System.out.format("copyToS3 %s => %s \n", file.getPath(),path);
					HDFSHelper.copyToHDFS(is,fs,path);
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			
			@Override
			public void actionDir(File dir) {
				String path = dir.getPath();
				if (!fileSeparator.equals("/"))
					path = path.replace(fileSeparator, "/");
				
				try {
					HDFSHelper.mkdir(fs, path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		put.run(dir, true);
	}

	/*
	 * public static boolean existBucket(AmazonS3 s3,String bucketName){
	 * List<Bucket> list= s3.listBuckets(); for (Bucket bucket : list) { if
	 * (bucket.getName().equals(bucketName)) return true; } return false; }
	 * public static void copyToS3(InputStream input,AmazonS3 s3,String
	 * bucketName,String key,long size) throws IOException { ObjectMetadata
	 * metadata=new ObjectMetadata(); metadata.setContentLength(size);
	 * PutObjectRequest req=new PutObjectRequest(bucketName, key,
	 * input,metadata); s3.putObject(req); }
	 */
	public static InputStream getInputStreamFormLocal(String path)
			throws IOException {
		return new FileInputStream(path);
	}

}
