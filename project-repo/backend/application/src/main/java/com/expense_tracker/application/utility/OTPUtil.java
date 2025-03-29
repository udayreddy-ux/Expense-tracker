package com.expense_tracker.application.utility;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OTPUtil {
	
	private static final SecureRandom random = new SecureRandom();
	private static final int OTP_LENGTH=6;
	
	public static String generateOtp() {
		StringBuilder otp=new StringBuilder();
		for(int i=0;i<OTP_LENGTH;i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}
}
