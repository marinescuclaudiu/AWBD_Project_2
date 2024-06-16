package com.awbd.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
//@EnableZipkinServer
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

//	http://localhost:8071/actuator/gateway/routes

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p
						.path("/unibuc/product/**")
						.filters(f -> f.rewritePath("/unibuc/product/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString()))
						.uri("lb:/PRODUCT")) //ln load balancer + application_name
				.route(p -> p
						.path("/unibuc/inventory/**")
						.filters(f -> f.rewritePath("/unibuc/inventory/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString()))
						.uri("lb://DISCOUNT"))
				.route(p -> p
						.path("/unibuc/order/**")
						.filters(f -> f.rewritePath("/unibuc/order/(?<segment>.*)","/${segment}")
								.addResponseHeader("X-Response-Time",new Date().toString()))
						.uri("lb:/ORDER"))
				.build();
	}

}
