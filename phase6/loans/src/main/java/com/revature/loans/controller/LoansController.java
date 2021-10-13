package com.revature.loans.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.loans.config.LoansServiceConfig;
import com.revature.loans.model.Customer;
import com.revature.loans.model.Loans;
import com.revature.loans.model.Properties;
import com.revature.loans.repository.LoansRepository;

@RestController
public class LoansController {

	@Autowired
	private LoansRepository loansRepository;
	

	@Autowired
	LoansServiceConfig loansConfig;

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
	
	@GetMapping("/loans/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(loansConfig.getMsg(), loansConfig.getBuildVersion(),
				loansConfig.getMailDetails(), loansConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}


}
