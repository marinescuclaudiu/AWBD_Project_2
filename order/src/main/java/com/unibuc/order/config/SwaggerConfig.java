package com.unibuc.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // http://localhost:8081/swagger-ui.html

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Spring Cloud application API for order-service")
                .version("1").description("ORDER-SERVICE with Spring Cloud Architecture"));

    }
}