package com.fsoft.fsa.kindergarten.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ComponentConfiguration {
    //config swagger
    @Bean
    public OpenAPI openAPI(@Value("${open.api.title}")  String title,
                           @Value("${version}")  String version,
                           @Value("${description}")  String description,
                           @Value("${serverUrl}")  String serverUrl,
                           @Value("${serverName}")  String serverName
                       ) {
        return new OpenAPI().info(
                        new Info().title(title)
                                .version(version)
                                .description(description)
                                .license(new License().name("API License").url("http://domain.vn/license")))
                .servers(List.of(new Server().url(serverUrl).description(serverName)));
    }
        @Bean
        public GroupedOpenApi groupedOpenApi() {
            return GroupedOpenApi.builder()
                    .group("api-service-1")
                    .packagesToScan("com.fsoft.fsa.kindergarten.controller")
                    .build();
        }

        @Bean
        public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEADER", "OPTIONS")
                        .allowedHeaders("*");
            }
         };
        }
}
