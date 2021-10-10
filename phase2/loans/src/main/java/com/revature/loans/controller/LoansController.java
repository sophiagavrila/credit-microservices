package com.revature.loans.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.loans.model.Customer;
import com.revature.loans.model.Loans;
import com.revature.loans.repository.LoansRepository;

@RestController
public class LoansController {

	@Autowired
	private LoansRepository loansRepository;

	/**
	 * Return all Loans objects that belong to a customer based on the Customer Id
	 * of the object passed through as the HTTP Request Body. 
	 */
	@PostMapping("/myLoans")
	public List<Loans> getLoansDetails(@RequestBody Customer customer) {
		
		List<Loans> loans = loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
		
		if (loans != null) {
			return loans;
		} else {
			return null;
		}

	}


}
