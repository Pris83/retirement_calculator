package com.example.retirementCalculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Retirement Planner API")
                        .version("1.0")
                        .description("Calculate and manage retirement savings plans"))
                .servers(List.of(new Server().url("http://localhost:8080")));
    }

    @Bean
    public GroupedOpenApi retirementApi() {
        return GroupedOpenApi.builder()
                .group("retirement-plans")
                .pathsToMatch("/retirement-plans/**")
                .build();
    }
}
