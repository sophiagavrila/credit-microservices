package com.revature.accounts.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.accounts.config.AccountsServiceConfig;
import com.revature.accounts.model.Accounts;
import com.revature.accounts.model.Cards;
import com.revature.accounts.model.Customer;
import com.revature.accounts.model.CustomerDetails;
import com.revature.accounts.model.Loans;
import com.revature.accounts.model.Properties;
import com.revature.accounts.repository.AccountsRepository;
import com.revature.accounts.service.client.CardsFeignClient;
import com.revature.accounts.service.client.LoansFeignClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.annotation.Timed;

@RestController
public class AccountsController {

	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	AccountsServiceConfig accountsConfig;

	@Autowired
	LoansFeignClient loansFeignClient;

	@Autowired
	CardsFeignClient cardsFeignClient;

	/**
	 * Passes customer object as parameter in HTTP Request body and returns Account
	 * object based on account found by that cusomter's ID.
	 */
	// Add it here - We have names, and described the metric to help us understand how
	// long it takes to receive a request and return a response.  This configures & exposes a new endpoint.
	@PostMapping("/myAccount")
	@Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")
	public Accounts getAccountDetails(@RequestBody Customer customer) {

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (accounts != null) {
			return accounts;
		} else {
			return null;
		}
	}

	/**
	 * This method will return all properties configured for this service from the
	 * auto-wired AccountsServiceConfig in JSON format to the client.
	 */
	@GetMapping("/account/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
				accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}

	/**
	 * @param correlationid - add the correlationId from the Request Header, which
	 *                      is generated by our Gateway pre-filters, forward it to
	 *                      Loans & Cards controller to track request throughout app.
	 */
	@PostMapping("/myCustomerDetails")
	@CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomerDetailsFallBack")
	public CustomerDetails myCustomerDetails(@RequestHeader("bank-correlation-id") String correlationid,
			@RequestBody Customer customer) {

		logger.info("myCustomerDetails() method started");

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(correlationid, customer);
		List<Cards> cards = cardsFeignClient.getCardDetails(correlationid, customer);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		customerDetails.setCards(cards);

		logger.info("myCustomerDetails() method ended");

		return customerDetails;
	}

	/**
	 * @param correlationid - Capture the correlationId from the Request Header and forward it here as well.
	 */
	private CustomerDetails myCustomerDetailsFallBack(@RequestHeader("bank-correlation-id") String correlationid,
			Customer customer, Throwable t) {

		// pass the correlationId to each method within controller to track request
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(correlationid, customer);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);

		return customerDetails;
	}

	/**
	 * @RateLimiter corresponds to the throttling threshold we set in
	 *              application.properties. Throttling is the process of limiting
	 *              the rate that an API is being used in a server.
	 */
	@GetMapping("/sayHello")
	@RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
	public String sayHello() {
		return "Hello Kubernetes!";
	}

	private String sayHelloFallback(Throwable t) {
		return "This is a RateLimiter Fallback!";
	}

}
