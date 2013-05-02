package fajoy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringUtils;

public class S3Helper {

	public static AmazonS3 getS3(){
		BotoProperties conf=BotoProperties.Constants;
		String accessKey=conf.aws_access_key_id;
		String secretKey=conf.aws_secret_access_key;
		String endpoint=((Boolean.parseBoolean(conf.is_secure))?"https://":"http://")+conf.host;
		AmazonS3 s3=getS3(accessKey, secretKey, endpoint);  
		return s3;
	}
	public static AmazonS3 getS3(String accessKey,String secretKey ,String endpoint){
	      AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	        AmazonS3 s3 = new AmazonS3Client(credentials);
	        s3.setEndpoint(endpoint);
	        S3ClientOptions clientOptions =new S3ClientOptions();
	        clientOptions.setPathStyleAccess(true);
	        s3.setS3ClientOptions(clientOptions );
		return s3;
	}
	public static InputStream getInputStreamFormS3(AmazonS3 s3,String bucketName,String key) throws IOException {
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
		return object.getObjectContent();
	}
	public static void copyToS3(InputStream input,AmazonS3 s3,String bucketName,String key,long size) throws IOException {
		ObjectMetadata metadata=new ObjectMetadata();
		metadata.setContentLength(size);
		PutObjectRequest req=new PutObjectRequest(bucketName, key, input, metadata);
		s3.putObject(req);		
	}
    public static boolean existBucket(AmazonS3 s3,String bucketName){
    	List<Bucket>  list=  	s3.listBuckets();
    	for (Bucket bucket : list) {
			if (bucket.getName().equals(bucketName))
				return true;
		}
    	return false;
    }
	public static void main(String[] args) {
		String bucketName="test.20130502182644";
		AmazonS3 s3=getS3();
				

		S3ObjectAction obj=new S3ObjectAction() {
			@Override
			public void actionObject(AmazonS3 s3, S3ObjectSummary obj) {
				
				System.out.format("%s %10s\t%s\n",StringUtils.fromDate(obj.getLastModified()),obj.getSize(),obj.getKey());				
			}
		};
		obj.run(s3, bucketName, "",null, false);
		
	
	
	}
	public static String getDirName(String path){
		int li= path.lastIndexOf('/');
		if (li==-1)
			return "";
		return path.substring(0,li);	
	}
	public static void listS3Prefix(AmazonS3 s3,String bucketName,String path){
		S3ObjectAction ls=new S3ObjectAction() {
			@Override 
			public void actionPrefix(AmazonS3 s3, String commonPrefixes) {
				System.out.format("%s \n",commonPrefixes);
			}
		};
		ls.run(s3, bucketName, path,"/",false);	
	}
	public static void listS3Object(AmazonS3 s3,String bucketName,String path){
		S3ObjectAction ls=new S3ObjectAction() {
			@Override 
			public void actionObject(AmazonS3 s3, S3ObjectSummary obj) {
				System.out.format("%s \n",obj.getKey());
			}
		};
		ls.run(s3, bucketName, path,"",false);		
	}
	
	public static void delS3Object(AmazonS3 s3,String bucketName,String prefix){
		S3ObjectAction del=new S3ObjectAction() {
			@Override
			public void actionObject(AmazonS3 s3, S3ObjectSummary obj) {
				System.out.format("obj %s \n", obj.getKey());
				s3.deleteObject(obj.getBucketName(), obj.getKey());
			}
		};
		del.run(s3, bucketName, prefix,null,true);
	}
	
	
	/*
	public static void creaeTestObject(AmazonS3 s3,String bucketName){
		
		for (int i=0;i<50;i++)
		{
			String key=String.format("test/00001/%05d/", i);
			setObject(s3, bucketName, key, "",new ObjectMetadata());
		}		
	}*/
	public static void showBucket(AmazonS3 s3){
	    List<Bucket> buckets = s3.listBuckets();
        for (Bucket bucket : buckets) {
                    System.out.println(bucket.getName() + "\t" + StringUtils.fromDate(bucket.getCreationDate()));
    }
	}
	
	public static void setObject(AmazonS3 s3,String bucketName ,String key,String value,ObjectMetadata metadata){
	    try {
	    	byte[] bs= value.getBytes("UTF-8");
	    	java.io.InputStream is =org.apache.commons.io.IOUtils.toInputStream(value);
			metadata.setContentLength(bs.length);
	    	PutObjectRequest req=new PutObjectRequest(bucketName, key, is,metadata);
			s3.putObject(req);
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		
	}
	
	
}

