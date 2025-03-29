package com.expense_tracker.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void printEnvVars() {
    		System.out.println("🔍 DB_URL = " + System.getenv("DB_URL"));
    		System.out.println("🔍 DB_USERNAME = " + System.getenv("DB_USERNAME"));
    		System.out.println("🔍 DB_PASSWORD = " + System.getenv("DB_PASSWORD"));

    		System.out.println("🔍 REDIS_HOST = " + System.getenv("REDIS_HOST"));
    		System.out.println("🔍 REDIS_PORT = " + System.getenv("REDIS_PORT"));
    		System.out.println("🔍 REDIS_PASSWORD = " + System.getenv("REDIS_PASSWORD"));

    		System.out.println("🔍 MAIL_USERNAME = " + System.getenv("MAIL_USERNAME"));
    		System.out.println("🔍 MAIL_PASSWORD = " + System.getenv("MAIL_PASSWORD"));

   		 System.out.println("🔍 AWS_ACCESS_KEY = " + System.getenv("AWS_ACCESS_KEY"));
    		System.out.println("🔍 AWS_SECRET_KEY = " + System.getenv("AWS_SECRET_KEY"));
	}
}
