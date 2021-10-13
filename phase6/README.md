# Phase 6: Routing & Cross Cutting Concerns with Spring Cloud Gateway
Spring Cloud Gateway is a library for building an API gateway, so it looks like any other Spring Boot application.  If you're a Spring developer, you'll find it's very easy to get started with Spring Cloud Gateway with just a few lines of code.

**Spring Cloud Gateway** is intended to sit between a requester and a resource that's being requested, where it intercepts, analuzes, and modifies every request.  That means you can route requests based on their context.  Did a request include a header indicating an API version?  We can route that request to the appropriately versioned backend.  Does the reuqest require sticky sessions?  The gateway can keep track of each user's session.

Spring Cloud Gateway is a replacement of **Zuul** for the following reasons:
    - Spring Cloud Gateway is the preferred API gateway implementation from the Spring Cloud Team.  It's built on Spring 5, Reactor, and Spring WebFlux.  Not only that, it also includes circuit breaker integration & service disvoery with Eureka.
    - Spring Cloud Gateway is non-blocking in nature. 
    - Spring Cloud Gateway is regarded as having superior performance to that of Zuul. 

<br>

## Build a Spring Cloud Gateway Service

1. Create a new Spring Boot starter project > Maven, Java 8, JAR > group: `com.revature` > Artifact: `gatewayserver` > package: `com.revature.gatewayserver`

2. Add the following dependencies:
    - **Gateway**
    - **Spring Boot Actuator**
    - **Eureka Discovery Client**
    - **Config Client**
    - **DevTools**

3. In the `pom.xml`, add the buildpack config tags for building an image:

<br>

```xml
<configuration>
    <image>
        <name>your-username/${project.artifactId}</name>
    </image>
</configuration>
```

<br>

4. Go to the main class of the app, `GatewayserverApplication.java` > add the annotation `@EnableEurekaClient` to ensure that we register this service to Eureka Serivce Discovery.

5. Open `application.properties` and add the following:

<br>

```yaml
# The name that will identify this service
spring.application.name=gatewayserver

# Interact with config server to import gatewayserver.properties
spring.config.import=optional:configserver:http://localhost:8071

# Expose all endpoints for Actuator
management.endpoints.web.exposure.include=*

## Configuring meta-data about app
info.app.name=Gateway Server Microservice
info.app.description=Bank Gateway Server Application
info.app.version=1.0.0

# Allows gateway to check all registry information from eureka re: other microservices
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true

# Log DEBUG statements
logging.level.com.eaztbytes.gatewayserver: DEBUG
```
<br>

6. Run configserver, eureka, accounts, gatewayserver > navigate to `http://localhost:8070` to view the 2 instances running of Eureka dashboard. 
> *With Gateway Server we are able to invoke services without knowing their location/IP*.  

<br>

6a. In the browser, go to `http://localhost:8072/actuator` (these are the endpoints for `gatewayserver`) > search for `gateway {}`.  Grab the URL and append `/routes` like so: `http://localhost:8072/actuator/gateway/routes`

<br>

> *You will see that Gateway Server has automatically integrated with Eureka Server and has all of the information about where the other microservices are*.  *You should see `accounts` information.  Gateway Server can use this informatino to re-route requests.*

<br>

7. Open postman, send a POST request with the body as `{ "customerId" : 1 }` to `http://localhost:8072/ACCOUNTS/myAccount` > Gateway interacted with eureka and understood that that was directed to `accounts` > the `accounts` microservice was invoked and returned a response.

8. Notice you had to send that request to a URL including letters IN ALL CAPS.  TO change this add the following line to `gatewayserver`'s `application.properties` to allow lowercase letters in the URL to route to the service that is expected to deliver the response.

<br>

```yaml
spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true
```

<br>

#### *But this seemes arbitrary, why add Gateway Server as an extra layer to our services?*
Gateway Server is powerful in the fact that it handles cross-cutting concerns like logging and authentication within our Microservice ecosystem.  We'll get to that in just a bit:

<br>

## Implement Custom Routing with Spring Cloud Gateway
Now we will take a scenario where we have a custom routing requirement.  If you go to `http://llocalhost:8072/actuator/gateway/routes` you will see all of the routes that Gateway Server is aware of.

Similarly, you can navigate to `http://llocalhost:8072/accounts/myCustomerDetails` and properly invoke `accounts`, `cards`, and `loans` as long as they're all running

Our challenge now is to create a custom route by injecting "bank" into the URL even though this doesn't exist, so that client can send a request somewhere like `http://localhost:8072/bank/accounts/myCustomerDetails` (we haven't configured this in our code).

1. Go to `com.revature.gatewayserver.GatewayserverApplication.java` > add the following bean to the class which defines all of your routes:

<br>

```java
/**
    * In each route below we pass a predicate which will check if each given path
    * satisfies a certain condition or not. 
    * 
    * In the first one for example, we're checking to see if a client sent a request to
    * "bank/accounts", if so, we re-write the route by removing the bank portion,
    * add a cutom header, and send it towards the appropriate service.
    */
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
            // We're checking to see if a client sent a request to "bank/accounts"
            .route(p -> p.path("/bank/accounts/**")
                    // If so, we re-write the route by removing the bank part 
                    .filters(f -> f.rewritePath("/bank/accounts/(?<segment>.*)", "/${segment}")
                            // Then we add a custom header including the Date
                            .addResponseHeader("X-Response-Time", new Date().toString()))
                    // And send it to the accounts service
                    .uri("lb://ACCOUNTS"))
            .route(p -> p.path("/bank/loans/**")
                    .filters(f -> f.rewritePath("/bank/loans/(?<segment>.*)", "/${segment}")
                            .addResponseHeader("X-Response-Time", new Date().toString()))
                    .uri("lb://LOANS"))
            .route(p -> p.path("/bank/cards/**")
                    .filters(f -> f.rewritePath("/bank/cards/(?<segment>.*)", "/${segment}")
                            .addResponseHeader("X-Response-Time", new Date().toString()))
                    .uri("lb://CARDS"))
            .build();
}
```

<br>

1. To check this out, run config > eureka > all other services > gateway (if you have them running already, you don't nedd to restart to see the changes you made in `gatewayserver` because you have DevTools as a dependency which automatically restarts for you) > navigate to `http://localhost:8072/bank/accounts/myAccount)`

*You should see the custom header returned by the server as well as the corresponding data from the `AccountsController`*

<br>

## Implement Tracing and Logging (Cross Cutting Concerns) with Spring Cloud Gateway
We will create a new requirement for this application: with each request that comes in, we will assign a **trace ID**.  A **trace ID** is a randomly generated ID that is a ssigned to a request. Using the trace ID, we can set up logging/auditing to check how many requests are coming in.  Using a trace ID, we can look into what level a request is reached to help us debug.

We will add a **prefilter** to assign a trace ID to each request.

1. Create a new package in `gatewayserver` called `com.revature.gatewayserver.filters` > create a class called `FilterUtility.java`

<br>

```java
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
```

</br>

2. Within `com.revature.gatewayserver.filters`, create a new class called `RequestTraceFilter` which implements `GlobalFilter` interface.  The class should look like this:

<b>

```java
/**
 * If you have multiple pre-filters, you can mention the order in which they
 * execute when a request comes in, chaining one after the other.
 */
@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

	private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);

    // Utilie FilterUtility to set Request Headers
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
```

<br>



<br>

1. Now we must add these `correlation id`'s to our controllers.  Go to `AccountsController` and update the methods as follows, so that our AccountsControllerknows the trace ID that's generated by the Gateway.  Edit the `myCustomerDetails()` method and its **fallback method**:

<br>

```java
/**
    * @param correlationid - add the correlationId from the Request Header, which
    *                      is generated by our Gateway pre-filters, forward it to
    *                      Loans & Cards controller to track request throughout app.
    */
@PostMapping("/myCustomerDetails")
@CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomerDetailsFallBack")
public CustomerDetails myCustomerDetails(@RequestHeader("bank-correlation-id") String correlationid,
        @RequestBody Customer customer) {

    logger.info("myCustomerDetails() method started");

    Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
    List<Loans> loans = loansFeignClient.getLoansDetails(correlationid, customer);
    List<Cards> cards = cardsFeignClient.getCardDetails(correlationid, customer);

    CustomerDetails customerDetails = new CustomerDetails();
    customerDetails.setAccounts(accounts);
    customerDetails.setLoans(loans);
    customerDetails.setCards(cards);

    logger.info("myCustomerDetails() method ended");

    return customerDetails;
}

/**
    * @param correlationid - Capture the correlationId from the Request Header and forward it here as well.
    */
private CustomerDetails myCustomerDetailsFallBack(@RequestHeader("bank-correlation-id") String correlationid,
        Customer customer, Throwable t) {

    // pass the correlationId to each method within controller to track request
    Accounts accounts = accountsRepository.findByCustomerId(correlationid, customer.getCustomerId());
    List<Loans> loans = loansFeignClient.getLoansDetails(correlationid, customer);

    CustomerDetails customerDetails = new CustomerDetails();
    customerDetails.setAccounts(accounts);
    customerDetails.setLoans(loans);

    return customerDetails;
}
```

<br>

2. Update the abstract methods in `CardsFeignClient` and `LoansFeignClient` to process the correlation trace ID:

<br>

`CardsFeignClient`

```java
@FeignClient("cards")
public interface CardsFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "myCards", consumes = "application/json") 
	List<Cards> getCardDetails(@RequestHeader("bank-correlation-id") String correlationid, @RequestBody Customer customer);
}
```
<br>

`LoansFeignClient`

```java
@FeignClient("loans")
public interface LoansFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "myLoans", consumes = "application/json")
	List<Loans> getLoansDetails(@RequestHeader("bank-correlation-id") String correlationid, @RequestBody Customer customer);
}
```

<br>

3. Update the `CardsController` & `LoansController`  within their respective services to accept the new `@RequestHeader` since these FeignClients will call them:

<br>

`CardsController` in `cards` service

```java
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
```

<br>

`LoansController` in `loans` service

```java
@PostMapping("/myLoans")
public List<Loans> getLoansDetails(@RequestHeader("bank-correlation-id") String correlationid, @RequestBody Customer customer) {
    
    logger.info("getLoansDetails() method started");
    List<Loans> loans = loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
    logger.info("getLoansDetails() method ended");
    
    if (loans != null) {
        return loans;
    } else {
        return null;
    }
}
```

<br>

4. Now go *back* to your `gatewayserver`'s `filter` package and add one more class called `ReponseTraceFilter`.  This class' repsonsibility is to add the trace id to the Response Header so that the client can view the trace id as well:

<br>

```java
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
```

<br>

