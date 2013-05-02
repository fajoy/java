package fajoy;

import java.io.*;


import org.apache.commons.io.IOUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3GetSample {
	
	public static final String tmpPath=System.getProperty("java.io.tmpdir");
	public static void main(String[] args) throws IOException {
		
		AmazonS3 s3 = S3Helper.getS3();
		String bucketName="test.20130502182644";
		S3ObjectAction get=new S3ObjectAction() {
			@Override
			public void actionObject(AmazonS3 s3, S3ObjectSummary obj) {
				File dir=new File(tmpPath+"/"+obj.getBucketName()+"/"+S3Helper.getDirName(obj.getKey()));
				dir.mkdirs();				
				File f=new File(tmpPath+"/"+obj.getBucketName()+"/"+obj.getKey());
				System.out.format("Copy %s %s\n", obj.getKey(),f.getAbsolutePath());
				
				
				try {
					InputStream is=S3Helper.getInputStreamFormS3(s3, obj.getBucketName(), obj.getKey());
					OutputStream os = new FileOutputStream(f);
					IOUtils.copy(is, os);
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		get.run(s3, bucketName, ""	, "",false);
		
	}

}
