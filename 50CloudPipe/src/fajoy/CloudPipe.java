package fajoy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class CloudPipe {
	public static final SimpleDateFormat dateForm = new SimpleDateFormat("yyyyMMddHHmmss");
	public CloudPipe() {

	}
	
	public static void main(String[] args) throws Exception {
		AmazonS3 s3 = S3Helper.getS3();
		String bucketName="test.20130502182644";
		final FileSystem fs = FileSystem.get(new Configuration());
		final String dst = "sample"+dateForm.format(new Date())+"/";
			
		
		S3ObjectAction get=new S3ObjectAction() {
			@Override
			public void actionObject(AmazonS3 s3, S3ObjectSummary obj) {
				String dirPath=dst+"/"+S3Helper.getDirName(obj.getKey());
				String dstPath=dst+"/"+obj.getKey();
				try {
					HDFSHelper.mkdir(fs, dirPath);
					InputStream is=S3Helper.getInputStreamFormS3(s3, obj.getBucketName(), obj.getKey());				
					HDFSHelper.copyToHDFS(is,fs,dstPath);
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		};
		get.run(s3, bucketName, ""	, "",false);
    
		System.exit(0);
	}
	

}
