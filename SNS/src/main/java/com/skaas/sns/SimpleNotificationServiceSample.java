package com.skaas.sns;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

//import com.amazonaws.services.sns.model.MessageAttributeValue;
//import java.util.HashMap;
//import java.util.Map;

/**
 * This sample demonstrates how to send SMS through Amazon SNS using AWS SDK for Java.
 */
public class SimpleNotificationServiceSample {

	public static void main(String[] args) {

        AmazonSNS sns = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
        
        CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyNewTopic");
        CreateTopicResult createTopicResult = sns.createTopic(createTopicRequest);
        
        //print TopicArn
        System.out.println(createTopicResult);
        String topicArn = createTopicResult.getTopicArn();
        
        SubscribeRequest subRequest = new SubscribeRequest(topicArn, "email", "ravi.prasad@siemens.com");
        sns.subscribe(subRequest);
        
        System.out.println("After clicking verification link sent to your email, Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        
        //publish to an SNS topic
        String msg = "My text published to SNS topic with email endpoint";
        PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        PublishResult publishResult = sns.publish(publishRequest);
        
        //print MessageId of message published to SNS topic
        System.out.println("MessageId - " + publishResult.getMessageId());
        
        String message = "My SMS message";
        String phoneNumber = "+919176038030";
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();

        PublishResult result = sns.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
	}

}
