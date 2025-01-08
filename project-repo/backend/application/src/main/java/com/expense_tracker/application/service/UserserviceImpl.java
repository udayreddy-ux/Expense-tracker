package com.expense_tracker.application.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.expense_tracker.application.dao.UserRepository;
import com.expense_tracker.application.entity.Users;
import com.expense_tracker.application.utility.JwtUtil;
import io.jsonwebtoken.Claims;

@Service
public class UserserviceImpl implements Userservice{
	private UserRepository userRepository;
	private EmailService emailService;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public UserserviceImpl(UserRepository userRepository,EmailService emailService) {
	      this.userRepository = userRepository;
	      this.emailService=emailService;
	}
	public String registerUser(Users user) {
		if(userRepository.findByEmail(user.getEmail()) != null) {
			throw new IllegalArgumentException("Email already in use");
		}
		// Hash the password
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return "Registration sucessful, Please login with valid credentials!";
	}
	
	public String loginUser(String email, String password) {
	    Users user = userRepository.findByEmail(email);
	    if (user == null) {
	        throw new IllegalArgumentException("Invalid email");
	    }

	 // Print stored hash from the database
	    String storedHash = user.getPassword();

	    // Generate a bcrypt hash from the user's input password
	    String generatedHash = passwordEncoder.encode(password);

	    // Check if the password matches
	    if (!passwordEncoder.matches(password, storedHash)) {
	        throw new IllegalArgumentException("Invalid password");
	    }

	    return JwtUtil.generateToken(email);

	}
	
	public void handleForgotPassword(String email) {
		Users user=userRepository.findByEmail(email);
		if(user == null) {
			throw new IllegalArgumentException("No user found with this email.");
		}
		
		//Generate JWT token
		String token = JwtUtil.generateToken(email);
		
		//Send email
		String resetLink="http://localhost:3000/reset-password?token="+token;
		emailService.sendEmail(email, "Password Reset Request",
			    "Dear User,\n\n" +
			    	    "You have requested to reset your password. Click the link below to reset your password. This link will expire in 15 minutes:\n\n" +
			    	    resetLink + "\n\n" +
			    	    "If you did not request this, please ignore this email.\n\n" +
			    	    "Best regards,\n" +
			    	    "Team Expense Sage");
	}
	
	public void resetPassword(String token,String newPassword) {
		Claims claims=JwtUtil.validateToken(token);
		String email=claims.getSubject();
		
		Users user=userRepository.findByEmail(email);
		if(user == null) {
			throw new IllegalArgumentException("Invalid token or user not found.");
		}
		
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
}
