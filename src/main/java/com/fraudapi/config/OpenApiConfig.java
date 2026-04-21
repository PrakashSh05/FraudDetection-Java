package com.fraudapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 3.0 configuration.
 *
 * <p>Swagger UI: <a href="http://localhost:8080/swagger-ui.html">http://localhost:8080/swagger-ui.html</a>
 * <p>OpenAPI JSON: <a href="http://localhost:8080/api-docs">http://localhost:8080/api-docs</a>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fraudDetectionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fraud Detection API")
                        .description("""
                                Transaction API with real-time fraud detection.
                                
                                **Fraud Rules:**
                                - Rule 1: Transactions exceeding ₹50,000 are flagged automatically.
                                - Rule 2: More than 3 transactions within 5 minutes triggers a velocity flag.
                                
                                Flagged transactions are recorded but **do not** modify the user's balance.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Fraud Detection Team")
                                .email("dev@fraudapi.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
