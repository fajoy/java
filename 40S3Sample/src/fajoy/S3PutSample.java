package fajoy;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.services.s3.AmazonS3;

public class S3PutSample {
	public static final SimpleDateFormat dateForm = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public static void main(String[] args) throws IOException {

		final String bucketName = "test." + dateForm.format(new Date());
		final AmazonS3 s3 = S3Helper.getS3();

		// Directory path here
		String path = "src";
		if (!S3Helper.existBucket(s3, bucketName)) {
			System.out.format("create bucket : %s\n", bucketName);
			s3.createBucket(bucketName);
		}

		File dir = new File(path);
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
					S3Helper.copyToS3(is, s3, bucketName, path, file.length());
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
