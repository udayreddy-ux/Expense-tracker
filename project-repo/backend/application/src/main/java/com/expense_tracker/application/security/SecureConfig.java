package com.expense_tracker.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecureConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	public SecureConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
        .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs
        .cors(cors -> cors.configurationSource(request -> {
            var config = new org.springframework.web.cors.CorsConfiguration();
            config.addAllowedOrigin("http://localhost:3000"); // Allow frontend origin
            config.addAllowedMethod("*"); // Allow all HTTP methods
            config.addAllowedHeader("*"); // Allow all headers
            config.setAllowCredentials(true); // Allow cookies/auth headers
            return config;
        }))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/signup", 
            				 "/api/users/login",
            				 "/api/users/forgot-password",
            				 "/api/users/reset-password").permitAll() // Public endpoints
            .requestMatchers("/api/expenses","/api/auth").authenticated()
            .anyRequest().authenticated()// Protect other endpoints
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }
}


