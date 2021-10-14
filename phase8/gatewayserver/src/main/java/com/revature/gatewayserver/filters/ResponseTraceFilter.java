package com.revature.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

/**
 * This class receives the response to be sent to the client and adds the trace
 * id so that the client receives the trace id appendned in the pre-filter stage
 * by RequestTraceFilter.java
 */
@Configuration
public class ResponseTraceFilter {

	private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

	@Autowired
	FilterUtility filterUtility;

	@Bean
	public GlobalFilter postGlobalFilter() {
		return (exchange, chain) -> {
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {

				HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
				String correlationId = filterUtility.getCorrelationId(requestHeaders);

				logger.debug("Updated the correlation id to the outbound headers. {}", correlationId);
				
				// adding header so that the client can observe trace id as { "bank-correlationid" - "xxxxxx"}
				exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
			}));
		};
	}
}
