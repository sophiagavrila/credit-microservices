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







