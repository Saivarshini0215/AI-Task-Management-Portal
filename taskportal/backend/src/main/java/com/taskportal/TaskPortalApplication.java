package com.taskportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Task Portal Spring Boot application.
 * Enables auto-configuration and component scanning.
 */
@SpringBootApplication
public class TaskPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskPortalApplication.class, args);
    }
}
