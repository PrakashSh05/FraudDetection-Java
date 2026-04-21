package com.fraudapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Fraud Detection API.
 *
 * <p>Swagger UI: http://localhost:8080/swagger-ui.html
 * <p>H2 Console: http://localhost:8080/h2-console
 * <p>Health:     http://localhost:8080/actuator/health
 */
@SpringBootApplication
public class FraudApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudApiApplication.class, args);
    }
}
