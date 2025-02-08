package com.expense_tracker.application.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense_tracker.application.entity.Users;
import com.expense_tracker.application.service.Userservice;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	public Userservice userService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody Users user){
		try {
			String response=userService.registerUser(user);
			return ResponseEntity.ok(response);
		}catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Users user){
		try {
			String token=userService.loginUser(user.getEmail(),user.getPassword());
			return ResponseEntity.ok("Bearer "+ token);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(401).body(e.getMessage());
		}
	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String,String> payload){
		String email=payload.get("email");
		try {
			userService.handleForgotPassword(email);
			return ResponseEntity.ok("Please check your email");
		}
		catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String,String> payload){
		String token=payload.get("token");
		String newPassword=payload.get("newPassword");
		try {
			userService.resetPassword(token, newPassword);
			return ResponseEntity.ok("Password reset successful.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/Profile")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getProfileDetails(){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			Users user=userService.getProfileDetails(userEmail);
			return ResponseEntity.ok(user);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/updateProfile")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> updateUserProfile(@Valid @RequestBody Users user){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok(userService.updateUserProfile(userEmail, user));
	}
}
