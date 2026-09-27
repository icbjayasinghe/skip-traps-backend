package com.example.skiptrack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI skipTrackOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Skip Track Firestore API")
                        .description("Manage newcomer settlement checklists (countries -> provinces -> activities -> tasks) backed by Firestore.")
                        .version("v1.0.0"));
    }
}
