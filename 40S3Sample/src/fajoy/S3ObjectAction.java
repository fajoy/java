package fajoy;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public abstract class S3ObjectAction{
	public void run(AmazonS3 s3,String bucketName,String prefix,String delimiter,boolean recursive){
		begin(s3);
		sub_run(s3, bucketName, prefix, delimiter, recursive);
		end(s3);
	}
	void sub_run(AmazonS3 s3,String bucketName,String prefix,String delimiter,boolean recursive){
		ListObjectsRequest lor=new ListObjectsRequest(bucketName,prefix,null,delimiter,null);
		String marker=null;
		do {
			ObjectListing ol= s3.listObjects(lor);
			
			for (String commonPrefixes: ol.getCommonPrefixes()){
				actionPrefix(s3,commonPrefixes);
				if (recursive){
					sub_run(s3, bucketName, commonPrefixes, delimiter, recursive);
				}
			}
			
			for(S3ObjectSummary o: ol.getObjectSummaries() )
			{
				actionObject(s3,o);
			}	
			
			marker=ol.getNextMarker();
			lor.setMarker(marker);
		}while(marker!=null);
		
	}
	public void actionObject(AmazonS3 s3,S3ObjectSummary obj){}
	public void actionPrefix(AmazonS3 s3,String commonPrefixes){}
	public void begin(AmazonS3 s3){}
	public void end(AmazonS3 s3){}
	
}