package com.unibuc.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
                .ignoreAcceptHeader(false) // Consider Accept header
                .favorPathExtension(false) // Do not use file extension
                .mediaType("json", MediaType.APPLICATION_JSON) // Add JSON media type
                .mediaType("xml", MediaType.APPLICATION_XML); // Add XML media type
    }

}
