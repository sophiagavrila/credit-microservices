package com.revature.cards.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.cards.config.CardsServiceConfig;
import com.revature.cards.model.Cards;
import com.revature.cards.model.Customer;
import com.revature.cards.model.Properties;
import com.revature.cards.repository.CardsRepository;

@RestController
public class CardsController {
	
	@Autowired
	private CardsRepository cardsRepository;
	
	@Autowired
	CardsServiceConfig cardsConfig;

	@PostMapping("/myCards")
	public List<Cards> getCardDetails(@RequestBody Customer customer) {
		
		List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
		
		if (cards != null) {
			return cards;
		} else {
			return null;
		}
	}

	@GetMapping("/cards/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(cardsConfig.getMsg(), cardsConfig.getBuildVersion(),
				cardsConfig.getMailDetails(), cardsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}
	
	
}
