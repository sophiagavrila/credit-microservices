package com.revature.gatewayserver.filters;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * This Utility class is degined to be autowired into our pre-filter class, 
 * providing the capability of modifying requests so as to append a trace id
 */
@Component
public class FilterUtility {

	public static final String CORRELATION_ID = "bank-correlation-id";
	
	// This method helps us identify if the trace ID is present or not
	public String getCorrelationId(HttpHeaders requestHeaders) {
		if (requestHeaders.get(CORRELATION_ID) != null) {
			List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
			return requestHeaderList.stream().findFirst().get(); // returns the trace ID
		} else {
			return null;
		}
	}

	// This method will be invoked by setCorrelationId() below to modify the Request Header in our Pre-Filter class
	public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
		return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
	}

	// This method calls the above to modify Request Header and will be invoked within the Pre-filter
	public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
		return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
	}

}
