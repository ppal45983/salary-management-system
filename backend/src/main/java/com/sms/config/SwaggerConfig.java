package com.sms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI 3.0 / Swagger documentation.
 * Exposes API documentation at /swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Bearer token. Example: Bearer eyJhbGc...")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .info(new Info()
                        .title("Salary Management System API")
                        .version("1.0.0")
                        .description("Enterprise salary management platform for 10,000+ employees. " +
                                "Provides comprehensive REST APIs for:\n" +
                                "- Employee CRUD operations\n" +
                                "- Salary processing with tax calculations\n" +
                                "- Analytics and reporting\n" +
                                "- Audit trail and compliance")
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@salaryms.com")
                                .url("https://salaryms.com/support"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
