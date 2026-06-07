package com.demo.travelcardsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UAE Travel Card System API")
                        .version("1.0")
                        .description("A RESTful API for managing a public transit smart card system. " +
                                "Provides endpoints for card registration, balance management, and journey processing with dynamic fare calculation."));
    }
}
