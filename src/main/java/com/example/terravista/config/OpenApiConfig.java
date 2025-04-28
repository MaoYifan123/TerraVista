package com.example.terravista.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI terraVistaOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("TerraVista API")
                        .description("TerraVista旅游景点管理系统API文档")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("TerraVista Wiki Documentation")
                        .url("https://terravista.wiki.github.org/docs"));
    }
}