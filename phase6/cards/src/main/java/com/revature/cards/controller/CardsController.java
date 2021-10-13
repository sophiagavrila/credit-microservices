package com.revature.cards.controller;

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
import com.revature.cards.config.CardsServiceConfig;
import com.revature.cards.model.Cards;
import com.revature.cards.model.Customer;
import com.revature.cards.model.Properties;
import com.revature.cards.repository.CardsRepository;

@RestController
public class CardsController {

	private static final Logger logger = LoggerFactory.getLogger(CardsController.class);

	@Autowired
	private CardsRepository cardsRepository;

	@Autowired
	CardsServiceConfig cardsConfig;

	/**
	 * @param correlationid - received from AccountsController's invocation of
	 *                      CardsFeignClient in accounts service. This method takes
	 *                      in the forwarded trace ID as a Request Header.
	 */
	@PostMapping("/myCards")
	public List<Cards> getCardDetails(@RequestHeader("bank-correlation-id") String correlationid,
			@RequestBody Customer customer) {

		logger.info("getCardDetails() method started");
		List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
		logger.info("getCardDetails() method ended");

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
