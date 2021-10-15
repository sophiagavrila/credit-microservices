package com.revature.gatewayserver;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	/**
	 * In each route below we pass a predicate which will check if each given path
	 * satisfies a certain condition or not. 
	 * 
	 * In the first one for example, we're checking to see if a client sent a request to
	 * "bank/accounts", if so, we re-write the route by removing the bank portion,
	 * add a cutom header, and send it towards the appropriate service.
	 */
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				// We're checking to see if a client sent a request to "bank/accounts"
				.route(p -> p.path("/bank/accounts/**")
						// If so, we re-write the route by removing the bank part 
						.filters(f -> f.rewritePath("/bank/accounts/(?<segment>.*)", "/${segment}")
								// Then we add a custom header including the Date
								.addResponseHeader("X-Response-Time", new Date().toString()))
						// And send it to the accounts service
						.uri("lb://ACCOUNTS"))
				.route(p -> p.path("/bank/loans/**")
						.filters(f -> f.rewritePath("/bank/loans/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", new Date().toString()))
						.uri("lb://LOANS"))
				.route(p -> p.path("/bank/cards/**")
						.filters(f -> f.rewritePath("/bank/cards/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", new Date().toString()))
						.uri("lb://CARDS"))
				.build();
	}

}
