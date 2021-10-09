package com.revature.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.accounts.model.Accounts;
import com.revature.accounts.model.Customer;
import com.revature.accounts.repository.AccountsRepository;


@RestController
public class AccountsController {
	
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
