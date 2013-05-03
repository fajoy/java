package fajoy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.amazonaws.services.s3.AmazonS3;

public class HDFSToS3Sample {
	public static final SimpleDateFormat dateForm = new SimpleDateFormat("yyyyMMddHHmmss");
	public static void main(String[] args) throws IOException {
		final String bucketName = "test." + dateForm.format(new Date());
		final AmazonS3 s3 = S3Helper.getS3();


		if (!S3Helper.existBucket(s3, bucketName)) {
			System.out.format("create bucket : %s\n", bucketName);
			s3.createBucket(bucketName);
		}
		
		String src = "./";
		final FileSystem fs = FileSystem.get(new Configuration());
		Path path=new Path(src);
		
		String fullPath=fs.getFileStatus(path).getPath().toUri().getPath();
		HDFSAction get=new HDFSAction() {
			@Override
			public void actionFile(FileSystem fs, Path path,
					FileStatus fileStatus) {
				
				try {
					InputStream is=fs.open(path);
					String stcPath=fileStatus.getPath().toUri().getPath();
					System.out.format("copyToS3 %s => %s \n",stcPath ,stcPath);
					S3Helper.copyToS3(is, s3, bucketName, stcPath, fileStatus.getLen());
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		get.run(fs,new Path(src),true);
		

	}

}
