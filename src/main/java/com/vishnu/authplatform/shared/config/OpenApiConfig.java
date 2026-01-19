package com.vishnu.authplatform.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String ADMIN_API_KEY_SCHEME = "AdminApiKey";

    @Value("${spring.application.name:Auth Platform}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .description("Authentication and Identity Platform API providing user registration, " +
                                "email verification, and application management capabilities.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@authplatform.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://authplatform.com/license")))
                .servers(List.of(
                        new Server().url("/").description("Current server")))
                .components(new Components()
                        .addSecuritySchemes(ADMIN_API_KEY_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Admin-Api-Key")
                                .description("Admin API Key for administrative operations")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .displayName("Public APIs")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("Admin APIs")
                .pathsToMatch("/api/v1/admin/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .addSecurityItem(new SecurityRequirement().addList(ADMIN_API_KEY_SCHEME)))
                .build();
    }
}
