package com.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for Salary Management System Spring Boot application.
 * 
 * This application provides REST APIs for managing employee salaries,
 * tax calculations, and analytics for enterprise organizations.
 * 
 * @author Salary Management Team
 * @version 1.0
 */
@SpringBootApplication
public class SalaryManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalaryManagementSystemApplication.class, args);
	}
}
