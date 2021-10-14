package com.revature.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * If you have multiple pre-filters, you can mention the order in which they
 * execute when a request comes in, chaining one after the other.
 */
@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

	private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

	@Autowired
	FilterUtility filterUtility;

	/**
	 * We are overriding the filter() method from GlobalFilter Interface.
	 * 
	 * ServerWebExchange - gives us the capability to capture HTTP Request.
	 * GatewayFilterChain - allows us to process request and send it to another pre-filter.
	 * 
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
		// Check if a correlation id is present in the header
		if (isCorrelationIdPresent(requestHeaders)) {
			// log it
			logger.debug("Bank-correlation-id found in tracing filter: {}. ",
					filterUtility.getCorrelationId(requestHeaders));
		} else {
			// first time request from user - generate a random number as id
			String correlationID = generateCorrelationId();
			// set the correlation id inside of the request before it continues to hit our app
			exchange = filterUtility.setCorrelationId(exchange, correlationID);
			logger.debug("Bank-correlation-id generated in tracing filter: {}.", correlationID);
		}
		
		// hand's over the exchange object to the next pre-filter
		return chain.filter(exchange);
	}

	// checks for our automatically generated trace-id on a request
	private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
		if (filterUtility.getCorrelationId(requestHeaders) != null) {
			return true;
		} else {
			return false;
		}
	}

	// generates a random number to be injected into request as correlation id
	private String generateCorrelationId() {
		return java.util.UUID.randomUUID().toString();
	}

}
