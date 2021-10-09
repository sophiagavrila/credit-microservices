package com.revature.accounts.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.accounts.model.Accounts;
import com.revature.accounts.model.Customer;
import com.revature.accounts.repository.AccountsRepository;


@RestController
public class AccountsController {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	private AccountsRepository accountsRepository;

	/**
	 * Passes customer object as parameter in HTTP Request body and returns Account
	 * object based on account found by that cusomter's ID.
	 */
	@PostMapping("/myAccount")
	public Accounts getAccountDetails(@RequestBody Customer customer) {

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (accounts != null) {
			return accounts;
		} else {
			return null;
		}

	}


}
