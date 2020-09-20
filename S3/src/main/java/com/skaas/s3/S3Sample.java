package com.skaas.s3;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Sample {

        public static void main(String[] args) {
                
                AmazonS3 s3client = AmazonS3ClientBuilder
                		.standard()
                        .withRegion(Regions.US_EAST_2)
                        .withCredentials(new ProfileCredentialsProvider("default"))
                        .build();

                final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName("ravi-new-bucket-270819");
                ListObjectsV2Result	result = s3client.listObjectsV2(req);
			if (result.getKeyCount() > 0) {
				for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    System.out.println(objectSummary.getKey() + " --- " + objectSummary.getSize());
                } 
			} else {
				System.out.println("No files found");            
            }

        }

}
