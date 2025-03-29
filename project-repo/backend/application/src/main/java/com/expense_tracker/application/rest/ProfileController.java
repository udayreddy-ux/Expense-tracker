package com.expense_tracker.application.rest;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense_tracker.application.dao.UserRepository;
import com.expense_tracker.application.dto.OtpRequest;
import com.expense_tracker.application.dto.UserUpdates;
import com.expense_tracker.application.entity.Users;
import com.expense_tracker.application.service.ExpensesService;
import com.expense_tracker.application.service.OTPService;
import com.expense_tracker.application.service.SesEmailService;
import com.expense_tracker.application.service.SnsOtpService;
import com.expense_tracker.application.utility.OTPUtil;

@RestController
@RequestMapping("/api/auth")
public class ProfileController {
	
	@Autowired
	private OTPService otpService;
	
	@Autowired
	private OTPUtil otpGenerator;
	
	@Autowired
	private SnsOtpService snsOtpService;
	
	@Autowired
	private SesEmailService sesEmailService;
	
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UserRepository userRepository; 
    
    @Autowired
	private ExpensesService expenseService;
	
	@PostMapping("/generate")
	@PreAuthorize("isAuthenticated()")
	public String generateOTP() {
		try {
			String userEmail=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			String otp=otpGenerator.generateOtp();
			otpService.saveOTP(userEmail, otp);
			return "OTP generated for " + userEmail + ": " + otp;
		}
		catch(RuntimeException e) {
			return e.getMessage();
		}
	}
	
	@PostMapping("/verify")
	@PreAuthorize("isAuthenticated()")
	public String verifyOTP(@RequestBody OtpRequest otpRequest) {
		String userEmail=(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String otp = otpRequest.getOtp();
		String storedOtp=otpService.getOTP(otpRequest.getEmail());
		System.out.println("Stored data: "+storedOtp);
		System.out.println("Submitted data: "+otp);
		try {
		      if (storedOtp == null) {
		            return "OTP expired or not found. tryout after 5 minutes.";
		        }
		        if (otpService.hasExceededFailedAttempts(userEmail)) {
		            return "Too many failed attempts. Try again later.";
		        }
		        if (storedOtp.equals(otp)) {
		            otpService.deleteOTP(userEmail);
		            otpService.resetFailedAttempts(userEmail);
		            return "OTP verified successfully.";
		        } else {
		            otpService.incrementFailedAttempts(userEmail);
		            return "Invalid OTP! Attempts left: " + (3 - Integer.parseInt(redisTemplate.opsForValue().get("failed_otp_attempts:" + userEmail)));
		        }
		}
		catch(RuntimeException e) {
			return e.getMessage();
		}
  
	}
	
	@PostMapping("/send/sms")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> sendSmsOtp() {
	    try {
	        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        System.out.println("Authenticated Email: " + userEmail);

	        Users user = userRepository.findByEmail(userEmail);
	        if (user == null) {
	            System.out.println("User not found in DB for email: " + userEmail);
	            return ResponseEntity.status(404).body("User not found.");
	        }

	        String phoneNumber = user.getMobile();
	        if (phoneNumber == null || phoneNumber.isEmpty()) {
	            System.out.println("Mobile number not found for user: " + userEmail);
	            return ResponseEntity.status(404).body("Mobile number not set.");
	        }

	        String otp = otpGenerator.generateOtp();
	        otpService.saveOTP(userEmail, otp);

	        String messageId = snsOtpService.sendOtpSms(phoneNumber, otp);
	        return ResponseEntity.ok("OTP sent to " + phoneNumber + "! Message ID: " + messageId);
	    } catch (RuntimeException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("OTP is valid for 5 minutes. Enter the existing one or try again later.");
	    }
	}
	
	@PostMapping("/send/email")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> sendEmailOtp(@RequestBody UserUpdates emailRetrieve) {
	    try {
	        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        System.out.println("Authenticated Email: " + userEmail);
	        System.out.println(emailRetrieve.getEmail());
	        Users user = userRepository.findByEmail(userEmail);
	        if (user == null) {
	            System.out.println("User not found in DB for email: " + userEmail);
	            return ResponseEntity.status(404).body("User not found.");
	        }

	        if (userEmail == null || userEmail.isEmpty()) {
	            System.out.println("Email not found for user: " + userEmail);
	            return ResponseEntity.status(404).body("Email not set.");
	        }
	        
	        String otp = otpGenerator.generateOtp();
	        otpService.saveOTP(emailRetrieve.getEmail(), otp);

	        String responseMessage = sesEmailService.sendOtpEmail(emailRetrieve.getEmail(), otp);
	        return ResponseEntity.ok(responseMessage);
	    } catch (RuntimeException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("OTP is valid for 5 minutes. Enter the existing one or try again later.");
	    }
	}
	/*
	@PostMapping("/send/email")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> sendEmailOtp(@RequestBody UserUpdates emailRetrieve) {
	    try {
	        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        System.out.println("Authenticated Email: " + userEmail);
	        System.out.println(emailRetrieve.getEmail());
	        Users user = userRepository.findByEmail(userEmail);
	        if (user == null) {
	            System.out.println("User not found in DB for email: " + userEmail);
	            return ResponseEntity.status(404).body("User not found.");
	        }

	        if (userEmail == null || userEmail.isEmpty()) {
	            System.out.println("Email not found for user: " + userEmail);
	            return ResponseEntity.status(404).body("Email not set.");
	        }
	        /*
	        String otp = otpGenerator.generateOtp();
	        otpService.saveOTP(userEmail, otp);

	        String responseMessage = sesEmailService.sendOtpEmail(userEmail, otp);
	        return ResponseEntity.ok(responseMessage);*/
	        //expenseService.handleProfileChanges(emailRetrieve.getEmail());
	        //return ResponseEntity.ok("Please check your updated email");
	    //} catch (RuntimeException e) {
	      //  e.printStackTrace();
	       // return ResponseEntity.status(500).body("OTP is valid for 5 minutes. Enter the existing one or try again later.");
	    //}*/
	//}
}
