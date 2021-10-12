# Phase 5: Making Microservices Resilient with Circuit Breakers
Building a Microservice  application is not just about dividing the overall app into smaller components.  It also involves making sure that all services are working together.  Sometimes a service may have an issue in its networking, or become slow.  We must make our microservices **resilient** so that they can self-correct in the case that one microservice makes a call to another microservice that is not performing well or isn't working properly.

In the case that Microservice A calls on Microservice B, and Microservice B doens't respond within a set amount of time, we need some type of fall-back strategy.  This is called setting up a **Circuit Breaker Pattern**.

We will use a **Circuit Breaker** to answer these three questions regarding Microservices: <br>
    - *How do we avoid cascading failure?*
    - *How do we handle failures gracefully with fallbacks?*
    - *How do we make our services capable of self-healing?*

Spring uses **Resilience4j Framework**.  Resilience4j is a lightweight, easy-to-user fault tolerance library inspired by **Netflix Hystrix**, but designed for Java 8 & functional programming.  

**Resilience4j**  offers the following patterns for increasing fault tolerance due to network problems or failure of any of the multiple services.
    - **Circuit breaker** - Used to stop marking requests when the invoked service is failing
    - **Fallback** - Slternative paths to failing requests.
    - **Retry** - Used to make retires when a service has temporarily failed
    - **Rate limit** - Limits the number of calls that a service receives in a time.
    - **Bulkhead** - Limits the number of outgoing concurrent requests to a service to avoid overloading.

*Before Resilience4j, everyone used to use **Hystrix**...but now Hystrix is in maintenence mode, so people use Resilience4j which has more features.*

<br>

## Circuit Breaker Pattern
In a distributed environemnt, calls to remote resources and services can fail due to transient faults, such as slow network connections, timeouts, or the resources being overcommited or temporarily unavailable.  These faults typically correct themselves after a short period of time, and a robust cloud application should be prepared to handle them.

The **Circuit Breaker Pattern**, which is inspired from the electrical circuit breakers, will monitor the remote calls.  If the calls take too long, the circuit breaker will intercede and kill the call.  Also, the circuit breaker will monitor all calls to a remote resource, and if enough calls cail, the circuit brek implementation will pop, failing fast and preventing calls to the failing remote resource.

The advantages of the circuit breaker pattern are:
- *Fail fast* :check:
- *Fail gracefully* :check:
- *Recover seamlessly* :check:

<br>

# Implementing Circuit Breaker Pattern
Since `accounts` invokes `cards` and `loans`, we will implement Circuit Breaker pattern ontop of the Feign Client API call in `AccountsController` in the case that one of the invoked services is down.

1. In `accounts` pom.xml, add these dependencies:
    - **Resilience4j**
    - **Resilience4j Time Limiter**
    - **Resilience4j Circuit Breaker**
    - **Spring AOP**

<br>

```xml
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot2</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-circuitbreaker</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-timelimiter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
```

<br>

2. Run a quick Maven Update.

3. Go to `AccountsController` add the annotation `@CircuitBreaker(name = "detailsForCustomerSupportApp")`

<br>

```java
	@PostMapping("/myCustomerDetails")
	@CircuitBreaker(name = "detailsForCustomerSupportApp")
	public CustomerDetails myCustomerDetails(@RequestBody Customer customer) {
		
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		List<Cards> cards = cardsFeignClient.getCardDetails(customer);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		customerDetails.setCards(cards);

		return customerDetails;
	}
```

<br>

4. Go to accounts `application.properties` to set the default behavior of the Circuit Breaker. Add these 5 props:

<br>

```yaml
# Send this info to be viewable by Actuator
resilience4j.circuitbreaker.configs.default.registerHealthIndicator= true
# Monitor a minimum of 5 requests to determine whether to keep it open (default is 50)
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.minimumNumberOfCalls= 5
# Threshold to consider to open circuit (if 50% of calls fail, open the alternative circuit)
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.failureRateThreshold= 50
# Property that tells how much time circuit breaker has to wait in order to decide to half-open the circuit (30 seconds, default it 60)
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.waitDurationInOpenState= 30000
# When circuit breaker is in half-open state, allow only 2 requests (if both are succsessful, clsoe alternative circuit)
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.permittedNumberOfCallsInHalfOpenState=2
```

<br>

## Start all services but simulate a scenario in which `cards` is not running

1. start config > eureka > accounts > loans ... but no cards

2. Confirm on eureka dashboard > open acutuator endpoint `localhost:8080/acutator` > you will see that `localhost:8080/actuator/circuitbreakers` is available to us and shows us information about the circuit breaker. Note: circuitBreakerEvents is empty.

<br>

```json
    "circuitbreakers-name": {
        "href": "http://localhost:8080/actuator/circuitbreakers/{name}",
        "templated": true
    },
    "circuitbreakers": {
        "href": "http://localhost:8080/actuator/circuitbreakers",
        "templated": false
    },
    "circuitbreakerevents": {
        "href": "http://localhost:8080/actuator/circuitbreakerevents",
        "templated": false
    }
```

<br>

*You will see that `http://localhost:8080/actuator/circuitbreakerevents` has no values.*

3. Send a post Request to `localhost:880/myCustomerDetails` 5 times >  then re-open `http://localhost:8080/actuator/circuitbreakerevents`.  You will see in the console: `io.github.resilience4j.circuitbreaker.CallNotPermittedException: CircuitBreaker 'detailsForCustomerSupportApp' is OPEN and does not permit further calls` and if you go back to `http://localhost:8080/actuator/circuitbreakerevents` you will see:

<br>

```json
{
    "circuitBreakerName": "detailsForCustomerSupportApp",
    "type": "FAILURE_RATE_EXCEEDED",
    "creationTime": "2021-10-12T09:27:41.643-04:00[America/New_York]",
    "errorMessage": null,
    "durationInMs": null,
    "stateTransition": null
}
```

<br>

*The circuit breaker will prevent any other requests to the API.  This is because the Circuit Breaker is OPEN and will not allow any other requests to go through.  It will allow you to try 3, but after it discovers that `cards` service is unresponsive, it prohibits any more calls.* *This is a scenario in which opur cirtuit breaker is **failing fast***.

*Now we want to adjust this circuit breaker so that we get at least `accounts` and `loans` information, even if we have to set `cards` details to null.  This is called a **Fallback Mechanism***.

<br>

## Implementing a Fallback Mechanism

1. In the Circuit Breaker annotation in `AccountsController`, add one more attribute over the `myCustomerDetails()`:

```java
@CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod ="myCustomerDetailsFallBack")
```

<br>

2. Now create a private method which is the name of our `fallbackMethod` attribute above.  Below, create the following method in your `AccountsController`

<br>

```java
	/**
	 * This method will accept the same request object as the above method.
	 * It will return both populated details for accounts and loans, but
	 * null values for cards in the case that service is down and Circuit Breaker
	 * has opened the circuit.
	 * 
	 * Throwable is helpful because the method is invoked due to an exception.
	 * (The FallBack method will not work without it).
	 */
	private CustomerDetails myCustomerDetailsFallBack(Customer customer, Throwable t) {
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		
		return customerDetails;
	}
```

<br>

3. Test it out! Start configserve > eurekaserver > aaccounts > loans...but not cards > send a post request to `localhost:8080:/myCustomerDetails`.

*You will notice on the **third** try, the circuit breaker has opened the fallback mechanism and returned all `accounts` and `loans` objects associates with that customerId, but null values for cards.*



