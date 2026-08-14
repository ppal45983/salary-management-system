package com.sms;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

	/**
	 * Configure OpenAPI 3.0 documentation for Swagger UI.
	 * Includes security scheme for JWT Bearer token authentication.
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Salary Management System API")
						.version("1.0.0")
						.description("Enterprise salary management platform for 10,000+ employees. " +
								"Provides comprehensive REST APIs for employee CRUD operations, " +
								"salary processing with tax calculations, and analytics.")
						.contact(new Contact()
								.name("SMS Support Team")
								.email("support@salaryms.com")
								.url("https://salaryms.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")))
				.components(new Components()
						.addSecuritySchemes("bearer-jwt", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("JWT Bearer token for API authentication")))
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
	}
}
