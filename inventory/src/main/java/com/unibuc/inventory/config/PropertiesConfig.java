package com.unibuc.inventory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("product")
@Getter
@Setter
public class PropertiesConfig {
    private String retailer;
    private String brand;
}