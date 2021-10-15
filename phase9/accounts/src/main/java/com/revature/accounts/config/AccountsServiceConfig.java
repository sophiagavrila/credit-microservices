/**
 * 
 */
package com.revature.accounts.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ConfigurationProperties reads all properties that begin with "accounts"
 */
@Configuration
@ConfigurationProperties(prefix = "accounts")
@Getter
@Setter
@ToString
public class AccountsServiceConfig {

	/**
	 * Store all properties fetches in an object
	 * and map to Java variables
	 */
	private String msg;
	private String buildVersion;
	private Map<String, String> mailDetails;
	private List<String> activeBranches;
}
