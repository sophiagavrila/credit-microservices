package com.bank.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication

// Checks the package to analyze package/annotations to create service available at Restful endpoints
@ComponentScans({ @ComponentScan("com.bank.accounts.controller")}) 

// Tells Spring Boot to scan all repository classes
@EnableJpaRepositories("com.bank.accounts.repository")

// Creates tables from all entity annotated classes.
@EntityScan("com.bank.accounts.model")
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

}
