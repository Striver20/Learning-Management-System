package com.example.lms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) Configuration
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access API docs at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Learning Management System API")
                        .version("1.0")
                        .description("Backend REST API for managing courses, enrollments, and content delivery. " +
                                "Features JWT authentication, role-based access control (Admin, Teacher, Student), " +
                                "AWS S3 file storage integration, and Quartz Scheduler for automated reminders.")
                        .contact(new Contact()
                                .name("LMS Support")
                                .email("support@lms.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.lms.com")
                                .description("Production Server (AWS EC2)")))
                .components(new Components()
                        .addSecuritySchemes("Bearer JWT",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"));
    }
}

