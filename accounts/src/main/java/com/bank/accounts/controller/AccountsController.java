package com.bank.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bank.accounts.model.Account;
import com.bank.accounts.model.Customer;
import com.bank.accounts.repository.AccountRepository;

@RestController
public class AccountsController {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@PostMapping("/myAccount")
	public Account getAccountDetails(@RequestBody Customer customer) {
		
		Account accounts = accountRepository.findByCustomerId(customer.getCustomerId());
		
		return (accounts != null) ? accounts : null;
		
	}
	
}
