package fajoy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.amazonaws.util.StringUtils;
import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class S3Sample {
    public static void main(String[] args) throws IOException {
        
        String accessKey = "";
        String secretKey = "";
        String endpoint="";
        
        String configFile = System.getProperties().getProperty("user.dir")+"/s3.properties";
        System.out.print(configFile);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        

        accessKey=properties.getProperty("accessKey");
        secretKey=properties.getProperty("secretKey");
        endpoint=properties.getProperty("endpoint");

        System.out.println(accessKey);
        System.out.println(secretKey);
        System.out.println(endpoint);
        
        String bucketName="sample.bucket";
        
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.setEndpoint(endpoint);
        S3ClientOptions clientOptions =new S3ClientOptions();
        clientOptions.setPathStyleAccess(true);
        s3.setS3ClientOptions(clientOptions );
        
        System.out.println("Creating bucket " + bucketName + "\n");
        s3.createBucket(bucketName);
        
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket bucket : buckets) {
                    System.out.println(bucket.getName() + "\t" +
                                            StringUtils.fromDate(bucket.getCreationDate()));
        }
        
        System.out.println("Listing objects");
        ObjectListing objectListing = s3.listObjects(bucketName);
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " +
                               "(size = " + objectSummary.getSize() + ")");
        }
        
        
        System.out.println("Uploading a new object to S3 from a file\n");
        String key = "MyObjectKey";
        s3.putObject(new PutObjectRequest(bucketName, key, createSampleFile()));
        
        System.out.println("Downloading an object");
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
        System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
        displayTextInputStream(object.getObjectContent());
        
        
        s3.deleteObject(bucketName, key);
        s3.deleteBucket(bucketName);

        
    }
    
    private static File createSampleFile() throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("01234567890112345678901234\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

        return file;
    }

    
    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            System.out.println("    " + line);
        }
        System.out.println();
    }

}

