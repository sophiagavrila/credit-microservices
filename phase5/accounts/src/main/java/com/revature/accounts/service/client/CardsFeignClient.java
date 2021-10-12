package com.revature.accounts.service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.revature.accounts.model.Cards;
import com.revature.accounts.model.Customer;

/**
 * This interface allows accounts to invoke a controller
 * method within the cards microservice.
 */
@FeignClient("cards") // use the application name that's registered in Eureka Server
public interface CardsFeignClient {

	// Indicate the path that you want to invoke (within cards' controller)
	@RequestMapping(method = RequestMethod.POST, value = "myCards", consumes = "application/json") 
	List<Cards> getCardDetails(@RequestBody Customer customer); // pass a customer obj, extract id, retrieve cards details
}
