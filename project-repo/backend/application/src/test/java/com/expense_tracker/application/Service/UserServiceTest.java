package com.expense_tracker.application.Service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import com.expense_tracker.application.dao.UserRepository;
import com.expense_tracker.application.entity.Users;
import com.expense_tracker.application.service.EmailService;
import com.expense_tracker.application.service.UserserviceImpl;
import com.expense_tracker.application.utility.JwtUtil;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	@Mock
	private EmailService emailService;
	@InjectMocks
	private UserserviceImpl userService;
	
	@Mock
	private JwtUtil jwtUtil;
	private Users testUser;
	@BeforeAll
	static void beforeAll() {
		System.out.println("Starting test suite...");
	}
	
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); 
		testUser = spy(new Users("John", "Doe", "john@example.com", "1234567890", "hashedPassword"));
	}
	
	@Test
	void testRegisterUser_Success() {
		
		//Arrange
		Users user = new Users("John","Doe","john@example.com","1234567890","plainpassword");
		
		when(userRepository.findByEmail("john@example.com")).thenReturn(null); //Email does not exist
		when(passwordEncoder.encode("plainpassword")).thenReturn("hashedpassword");
		
		//Act
		String result = userService.registerUser(user);
		
		//Assert
		assertEquals("Registration sucessful, Please login with valid credentials!",result);
		assertEquals("hashedpassword",user.getPassword());
		verify(userRepository).save(user); //verify user is saved
		
	}
	
	@Test
	void testRegisterUser_EmailAlreadyExists() {
		//Arrange
		Users existingUser=new Users("Jane","Doe","jane@example.com","0987654321","securepassword");
		when(userRepository.findByEmail("jane@example.com")).thenReturn(existingUser);
		
		//Act & Assert
	    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
	        userService.registerUser(new Users("Jane", "Doe", "jane@example.com", "0987654321", "securepassword"));
	    });
		
	    assertEquals("Email already in use",exception.getMessage());
	    
	    verify(userRepository,never()).save(any());
	}
	
	@Test
	void testLoginUser_Success() {
	    // Arrange
	    when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
	    when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
	    when(jwtUtil.generateToken("john@example.com")).thenReturn("mockToken");

	    // Act
	    String token = userService.loginUser("john@example.com", "password123");

	    // Assert
	    assertEquals("mockToken", token);
	    verify(userRepository).findByEmail("john@example.com");
	    verify(jwtUtil).generateToken("john@example.com");
	}

	
	@Test
	void testLoginUser_InvalidEmail() {
		//Act & Assert
		when(userRepository.findByEmail("bob@example.com")).thenReturn(null);
		
		Exception exception = assertThrows(IllegalArgumentException.class,()->{
			userService.loginUser("bob@example.com", "password123");
		});
		
		assertEquals("Invalid email",exception.getMessage());
		verify(userRepository).findByEmail("bob@example.com");
	}
	
	@Test
	void testLoginUser_InvalidPassword() {
		//Arrange: User exists, but password is incorrect
		when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
		when(passwordEncoder.matches("wrongPassword",testUser.getPassword())).thenReturn(false);
		
		//Act & Assert
		Exception exception = assertThrows(IllegalArgumentException.class,()->{
			userService.loginUser("john@example.com", "wrongPassword");
		});
		
		assertEquals("Invalid password",exception.getMessage());
		verify(userRepository).findByEmail("john@example.com");
	}
	@Test
	void testForgotPassword_Sucess() {
		
		// Arrange
		when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
		when(jwtUtil.generateToken("john@example.com")).thenReturn("mockToken");
		//Act
		String token =jwtUtil.generateToken("john@example.com");
		
		userService.handleForgotPassword("john@example.com");
		//Assert
		verify(emailService).sendEmail(eq("john@example.com"), eq("Password Reset Request"),argThat(message -> message.contains("mockToken")));
	}
	
	@Test
	void testHandleForgotPassword_UserNotFound() {
		//Arrange
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(null);
		//Act & Assert
		Exception exception = assertThrows(IllegalArgumentException.class,()->{
			userService.handleForgotPassword("test@gmail.com");
		});
		
		assertEquals("No user found with this email.",exception.getMessage());
		verify(emailService,never()).sendEmail(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testresetPassword_Success() {
		//Arrange
		Claims mockClaims = mock(Claims.class);
		when(mockClaims.getSubject()).thenReturn("john@example.com");
		when(jwtUtil.validateToken("mockToken")).thenReturn(mockClaims);
		when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
		when(passwordEncoder.encode("NewPassword")).thenReturn("ChangedPassword");
		
		doNothing().when(testUser).setPassword(anyString());
		
		//Act
		userService.resetPassword("mockToken", "NewPassword");
		
		//Assert
		verify(testUser).setPassword("ChangedPassword");
		verify(userRepository).save(testUser);

	}
	@Test
	public void testresetPassword_InvalidToken() {
		when(jwtUtil.validateToken("wrongToken")).thenThrow(new IllegalArgumentException("Invalid token or user not found."));
		Exception exception = assertThrows(IllegalArgumentException.class,()->{
			userService.resetPassword("wrongToken", "NewPassword");
		});
		
		//Assert
		assertEquals("Invalid token or user not found.", exception.getMessage());
		verify(testUser,never()).setPassword(anyString());
		verify(userRepository,never()).save(testUser);
	}
	
	@Test
	public void testgetProfileDetails_Success() {
		when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
		Users res=userService.getProfileDetails("john@example.com");
		assertEquals(testUser,res);
	}
	
	@Test
	public void testgetProfileDetails_InvalidEmail() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(null);
		Exception exception=assertThrows(IllegalArgumentException.class,()->{
			Users res=userService.getProfileDetails("test@example.com");
		});
		assertEquals("User not found",exception.getMessage());
	}
	
	@Test
	public void testupdateUserProfile_Success() {
		String email="john@example.com";
		Users updatedUser=new Users("John", "Smith", "johnsmith@example.com", "9876543210", "newpassword");
		
		when(userRepository.findByEmail(email)).thenReturn(testUser);
		when(userRepository.save(testUser)).thenReturn(testUser);
		
		//Act
		Users result = userService.updateUserProfile(email, updatedUser);
	       // Assert
        assertNotNull(result);
        assertEquals("johnsmith@example.com", result.getEmail());
        assertEquals("John", result.getFirst_name());
        assertEquals("Smith", result.getLast_name());
        assertEquals("9876543210", result.getMobile());
		
        verify(userRepository,times(1)).save(testUser);
	}
	
	
    @Test
    void testUpdateUserProfile_UserNotFound() {
  
        String email = "nonexistent@example.com";
        Users updatedUser = new Users("John", "Smith", "johnsmith@example.com", "9876543210", "newpassword");

        when(userRepository.findByEmail(email)).thenReturn(null); // User not found

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserProfile(email, updatedUser);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, never()).save(any());
    }
	@AfterAll
	static void afterAll() {
		System.out.println("Test suite completed...");
	}
}
