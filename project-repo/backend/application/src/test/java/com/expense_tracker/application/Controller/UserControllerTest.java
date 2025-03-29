package com.expense_tracker.application.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.expense_tracker.application.dao.UserRepository;
import com.expense_tracker.application.entity.Users;
import com.expense_tracker.application.rest.UserController;
import com.expense_tracker.application.service.Userservice;
import com.expense_tracker.application.utility.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc(addFilters=false)
@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private Userservice userService;
	
    @MockBean
    private JwtUtil jwtUtil; 
	
	private ObjectMapper objectMapper;
	
	private Users user;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		objectMapper = new ObjectMapper();
		user = new Users("John", "Doe", "john@example.com", "1234567890", "hashedPassword");
	}
	
	@Test
	void testSignup_Success() throws Exception{
		//Arrange
		when(userService.registerUser(any())).thenReturn("Registration sucessful, Please login with valid credentials!");
		
		//Act & Assert
		mockMvc.perform(post("/api/users/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andExpect(content().string("Registration sucessful, Please login with valid credentials!"));
	}
	
	@Test
	void testSignup_EmailAlreadyExists() throws Exception{
		//Arrange
		Users user = new Users("Jane","Doe","john@example.com","0987654321","password123");
		
		when(userService.registerUser(any())).thenThrow(new IllegalArgumentException("Email already in use"));
		
        // Act & Assert
        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use"));
	}
	
	@Test
	void testLoginUser_Success() throws Exception{
		//Arrange
		when(userService.loginUser("john@example.com","hashedPassword")).thenReturn("This is test token.");
		
		//Act and Assert
		mockMvc.perform(post("/api/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andExpect(content().string("Bearer " + "This is test token."));
	}
	
	@Test
	void testLoginUser_WrongPassword() throws Exception{
		//Arrange
		when(userService.loginUser(anyString(), anyString())).thenThrow(new IllegalArgumentException("Invalid password"));
		
		//Act & Assert
		mockMvc.perform(post("/api/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
        		.andExpect(status().isUnauthorized())
        		.andExpect(content().string("Invalid password"));
	}
	
	@Test
	void testForgotPassword_Success() throws Exception {
		
		//Arrange
		HashMap<String,String> payload = new HashMap<>();
		payload.put("email", "john@example.com");
		
		doNothing().when(userService).handleForgotPassword(eq("john@example.com"));
		
		//Act & Assert
		mockMvc.perform(post("/api/users/forgot-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(payload)))
        		.andExpect(status().isOk())
        		.andExpect(content().string("Please check your email"));
	}
	
	@Test
	void testForgotPassword_EmailNotFound() throws Exception{
		HashMap<String,String> payload=new HashMap<>();
		payload.put("email", "wrong@example.com");
		
		doThrow(new IllegalArgumentException("No user found with this email.")).when(userService).handleForgotPassword(anyString());
		
		//Act & Assert
		mockMvc.perform(post("/api/users/forgot-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(payload)))
        		.andExpect(status().isBadRequest())
        		.andExpect(content().string("No user found with this email."));
	}
	
	@Test
	void testResetPassword_Success() throws Exception {
	    // Arrange
	    HashMap<String, String> payload = new HashMap<>();
	    payload.put("token", "This is test token.");
	    payload.put("newPassword", "NewPassword");

	    // Mock the behavior
	    doNothing().when(userService).resetPassword("This is test token.", payload.get("newPassword"));

	    // Act & Assert
	    mockMvc.perform(post("/api/users/reset-password")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(payload)))
	            .andExpect(status().isOk())
	            .andExpect(content().string("Password reset successful."));
	}
	
	@Test
	void testResetPassword_InvalidToken() throws Exception {
	    // Arrange
	    HashMap<String, String> payload = new HashMap<>();
	    payload.put("token", "invalid-token");
	    payload.put("newPassword", "NewPassword");

	    // Mock void method to throw exception
	    doThrow(new IllegalArgumentException("Invalid token or user not found."))
	        .when(userService).resetPassword("invalid-token", payload.get("newPassword"));

	    // Act & Assert
	    mockMvc.perform(post("/api/users/reset-password")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(payload)))
	            .andExpect(status().isBadRequest())
	            .andExpect(content().string("Invalid token or user not found."));
	}
	
	@Test
	void testProfileDetails_Success() throws Exception{
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
	    UsernamePasswordAuthenticationToken authentication =
	            new UsernamePasswordAuthenticationToken("john@example.com", null, authorities);

	    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
	    securityContext.setAuthentication(authentication);
	    SecurityContextHolder.setContext(securityContext);
		String email=user.getEmail();
		when(userService.getProfileDetails(email)).thenReturn(user);
		mockMvc.perform(get("/api/users/Profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(user)));
	}
	 @Test
	void testProfileDetails_InvalidUser() throws Exception {
		 MockitoAnnotations.openMocks(this);
	     objectMapper = new ObjectMapper();
	     List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
	     UsernamePasswordAuthenticationToken authentication =
	                new UsernamePasswordAuthenticationToken("wrong@example.com", null, authorities);
	     SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
	     securityContext.setAuthentication(authentication);
	     SecurityContextHolder.setContext(securityContext);

	     when(userService.getProfileDetails(eq("wrong@example.com")))
	              .thenThrow(new IllegalArgumentException("User not found"));

	     mockMvc.perform(get("/api/users/Profile")
	              .contentType(MediaType.APPLICATION_JSON))
	              .andExpect(status().isBadRequest())
	              .andExpect(content().string("User not found"));
	 }
	 
	 @Test
	 void testupdateUserProfile_Success() throws Exception {
	     // Arrange
	     List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
	     UsernamePasswordAuthenticationToken authentication =
	             new UsernamePasswordAuthenticationToken("john@example.com", null, authorities);

	     SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
	     securityContext.setAuthentication(authentication);
	     SecurityContextHolder.setContext(securityContext);

	     String email = user.getEmail();

	     when(userService.updateUserProfile(eq(email), any(Users.class)))
	         .thenReturn(user);

	     // Act & Assert
	     mockMvc.perform(put("/api/users/updateProfile") 
	             .contentType(MediaType.APPLICATION_JSON)
	             .content(objectMapper.writeValueAsString(user)))
	             .andExpect(status().isOk())
	             .andExpect(content().json(objectMapper.writeValueAsString(user)));
	 }
}
