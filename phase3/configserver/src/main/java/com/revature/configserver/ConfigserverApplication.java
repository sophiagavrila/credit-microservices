package com.revature.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @EnableConfigServer makes this application a ConfigServer which will read
 *                     configuration properties from a centralized repository
 *                     and expose them as REST end-points to your other
 *                     microservices that have the ConfigClient dependency.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigserverApplication.class, args);
	}

}
