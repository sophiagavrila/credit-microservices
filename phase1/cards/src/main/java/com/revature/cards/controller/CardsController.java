package com.revature.cards.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.cards.model.Cards;
import com.revature.cards.model.Customer;
import com.revature.cards.repository.CardsRepository;

@RestController
public class CardsController {
	
	@Autowired
	private CardsRepository cardsRepository;

	@PostMapping("/myCards")
	public List<Cards> getCardDetails(@RequestBody Customer customer) {
		
		List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
		
		if (cards != null) {
			return cards;
		} else {
			return null;
		}

	}
}
