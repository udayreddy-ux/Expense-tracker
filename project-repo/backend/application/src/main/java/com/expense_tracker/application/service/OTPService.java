package com.expense_tracker.application.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class OTPService {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	//OTP expire time
	private static final long EXPIRE_TIME=5;
	private static final int MAX_OTP_ATTEMPTS=3;
	private static long ATTEMPT_WINDOW=5;
	private static final int MAX_FAILED_ATTEMPTS=3;
	
	public boolean canRequestOtp(String email) {
		String attemptsKey="otp_attempts:"+ email;
		String attempts=redisTemplate.opsForValue().get(attemptsKey);
		if(attempts!=null && Integer.parseInt(attempts) >=MAX_OTP_ATTEMPTS) {
			return false;
		}
		return true;
	}
	
	public void incrementOtpRequestCount(String email) {
		String attemptsKey="otp_attempts:"+email;
		
		
		redisTemplate.opsForValue().increment(attemptsKey);
		redisTemplate.expire(attemptsKey, ATTEMPT_WINDOW,TimeUnit.MINUTES);
	}
	public void saveOTP(String email, String otp) {
		String key = "otp:" + email;
	    if (redisTemplate.hasKey(key)) {
	        throw new RuntimeException("Please tryout after 5 minutes.");
	    }
	    
		if(!canRequestOtp(email)) {
			throw new RuntimeException("OTP request limit exceeded. Try again later.");
		}
		redisTemplate.opsForValue().set(key,otp,EXPIRE_TIME,TimeUnit.MINUTES);
		incrementOtpRequestCount(email);
		resetFailedAttempts(email);
	}
	
	public String getOTP(String email) {
		String key="otp:" + email;
		return redisTemplate.opsForValue().get(key);
	}
	
	public void deleteOTP(String email) {
		String key="otp:" + email;
		redisTemplate.delete(key);
	}
	
    public boolean hasExceededFailedAttempts(String email) {
        String failedAttemptsKey = "failed_otp_attempts:" + email;
        String attempts = redisTemplate.opsForValue().get(failedAttemptsKey);
        return (attempts != null && Integer.parseInt(attempts) >= MAX_FAILED_ATTEMPTS);
    }
    
    public void incrementFailedAttempts(String email) {
        String failedAttemptsKey = "failed_otp_attempts:" + email;
        redisTemplate.opsForValue().increment(failedAttemptsKey);
        redisTemplate.expire(failedAttemptsKey, ATTEMPT_WINDOW, TimeUnit.MINUTES);
    }
    
    public void resetFailedAttempts(String email) {
        String failedAttemptsKey = "failed_otp_attempts:" + email;
        redisTemplate.delete(failedAttemptsKey);
    }
    
    
}
