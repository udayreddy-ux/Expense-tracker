package com.expense_tracker.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
public class SesEmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.senderEmail}")
    private String senderEmail;

    @Value("${aws.ses.configurationSetName}")  
    private String configurationSetName;

    public SesEmailService(@Value("${aws.accessKey}") String accessKey,
                           @Value("${aws.secretKey}") String secretKey,
                           @Value("${aws.region}") String region) {
        this.sesClient = SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String sendOtpEmail(String recipientEmail, String otp) {
        String subject = "Your Expense Sage OTP Code";
        String body = "Dear User,\n\n" +
                "You have requested to edit your personal details on your Expense Sage account.\n\n" +
                "Your OTP is: " + otp + "\n\n" +
                "Please enter this OTP within 3 attempts. If you fail to verify, you'll need to request a new OTP.\n\n" +
                "Thank you,\n" +
                "Team Expense Sage";

        SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(recipientEmail).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder().text(Content.builder().data(body).build()).build())
                        .build())
                .source(senderEmail)
                .configurationSetName(configurationSetName)
                .build();

        sesClient.sendEmail(request);
        return "OTP sent successfully to " + recipientEmail;
    }
}