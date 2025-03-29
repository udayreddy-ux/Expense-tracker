package com.expense_tracker.application.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
public class SnsOtpService {
	private final SnsClient snsClient;
	private final String emailTopicArn;
	
	public SnsOtpService(@Value("${aws.accessKey}") String accessKey,
						 @Value("${aws.secretKey}") String secretKey,
						 @Value("${aws.region}") String region,
						 @Value("${aws.sns.email-topic-arn}") String emailTopicArn) {
		this.emailTopicArn=emailTopicArn;
		this.snsClient=SnsClient.builder()
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
				.build();
	}
	
	public String sendOtpSms(String phoneNumber,String otp) {
	    Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();

	   
	    smsAttributes.put("AWS.SNS.SMS.SMSType",
	        MessageAttributeValue.builder()
	            .stringValue("Transactional")
	            .dataType("String")
	            .build()
	    );

	    PublishRequest request = PublishRequest.builder()
	        .phoneNumber(phoneNumber)
	        .message("Your Expense Tracker OTP: " + otp)
	        .messageAttributes(smsAttributes)  
	        .build();

	    PublishResponse response = snsClient.publish(request);
	    return response.messageId();
	}
	

}
