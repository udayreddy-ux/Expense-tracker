package com.expense_tracker.application.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.expense_tracker.application.entity.Users;

public interface Userservice {
	
	String registerUser(Users user);
	String loginUser(String email,String password);
	void handleForgotPassword(String email);
	void resetPassword(String token,String newPassword);
	Users getProfileDetails(String email);
	Users updateUserProfile(String email,Users updatedUsers);
}
