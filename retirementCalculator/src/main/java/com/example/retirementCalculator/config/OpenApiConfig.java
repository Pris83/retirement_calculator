package com.example.retirementCalculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Retirement Planner API")
                        .version("1.0")
                        .description("Calculate and manage retirement savings plans"));
    }

    @Bean
    public GroupedOpenApi retirementApi() {
        return GroupedOpenApi.builder()
                .group("retirement-plans")
                .pathsToMatch("/retirement-plans/**")
                .build();
    }
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addRedirectViewController("/swagger-ui.html", "/swagger-ui/index.html");
            }
        };
    }
}
