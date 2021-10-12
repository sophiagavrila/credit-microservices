# Phase 4: Service Discovery & Registration with Eureka and Feign Client
Service Discovery & Registration deals with solving the problems that can occur when Microservices talk to eachother, i.e perform API calls.

In traditional network topology, applications have static network locations.  Hence IP addresses of relevant external locations can be read from a configuraion file, as these addresses rarely change.

In modern microservices architecture, knowing the right network location of an application is a much more complex problem for the clients as service instances might have dynamically assigned IP addressed.  Moreover, the number may vary due to autoscaling and failures.

Microservice Service Discovery & Registration is a way for applications and microservices to locate eachother on a network.  This includes:
    
- *A central server (or servers) that maintain a global view of addresses*
- *Microservices/clients that connect to the central server to register their address when they start & are ready*.    
- *Microservices/clients need to send their heartbeats at regular intervals to central server to inform on their health.*

<br>

Spring Cloud makes Service Discovery & Registry easy with the help of the below components:

- **Spring Cloud Netflix's Eureka service** which will act as a service discovery agent*

- **Spring Cloud Load Balancer library** for client-side load balancing**

- **Netflix Feign client** to look up services between microservices.

> * *We user Eureka because it's widely used, but there are other popular service registries such as **Consul**, **Apache Zookeeper**, and **etcd*** <br>
> ** *Netflix Ribbon used to be the go-to fo load balacning, but it is currentl;y in maintenance mode, so we will use Spring Cloud Load Balancer*.

<br>

## Setup Service Discovery Agent using Eureka Server

1. Generate a new Spring Starter Project in the same IDE as your other services > name it `eurekaserver` > group: `com.revature` > package: `com.revature.eurekaserver`

2.  Add these dependencies:
  - Spring Boot Actuator
  - Eureka Server
  - Spring Cloud Config Client

3. Modify the `pom.xml` to exclude Netflx Ribbon.  Add the `<exclusions>` featured below within the Spring Cloud Eureka Server dependency:

<br>

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.netflix.ribbon</groupId>
            <artifactId>ribbon-eureka</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

<br>

4. Under the Maven plugin, add the `<image>` tag to auto-generate a Docker file with Buildpacks:

<br>

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <name>sophiagavrila/${project.artifactId}</name>
                </image>
            </configuration>
        </plugin>
    </plugins>
</build>
```

<br>

5. Add the `@EnableEurekaServer` annotation above `com.revature.eurekaserver.EurekaServerAlication.java`

<br>

```java
/**
 * @EnableEurekaServer makes our Spring Boot microservice
 *                     act as a Service Discovery agent
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaserverApplication.class, args);
	}
}
```

<br>

6. In `application.properties`, add the following:

<br>

```yaml
# Properties necessary for Config Server
# This is the name used to identify this service in configserver
spring.application.name=eurekaserver
spring.config.import=optional:configserver:http://localhost:8071

# Add this to confirm that we are not using Ribbon which is deprecated
spring.cloud.loadbalancer.ribbon.enabled=false
```

<br>

7. Add the `eurekaserver` proeprties to the Config Server Git Repository [here](https://github.com/sophiagavrila/credit-microservices-config/tree/main)

8. Test it! Start the `configserver` first, then start `eurekaserver` > Go to `http://localhost:8070/` (fwded port from config)
> *You will see a dashboard of all instances of a service that's up (none right now)*

<br>

## Make changes to the microsservices to connect to Eureka
Each microservice can register itself to Eureka service discovery and send a heartbeat.

1. Start with `accounts` > open `pom.xml` and add the following dependencies:
    - **Eureka Discovery Client**
    - **OpenFeign**

2. Go to `application.properties` and add the properties to connect with `eurekaserver`:

<br>

```yaml
# In the case that the IP address for this container changes
eureka.instance.preferIpAddress = true 
# Go ahead and register with Eureka
eureka.client.registerWithEureka = true
# Fetch all registry details
eureka.client.fetchRegistry = true
eureka.client.serviceUrl.defaultZone = http://localhost:8070/eureka/


## Configuring info endpoint for actuator
info.app.name=Accounts Microservice
info.app.description=Eazy Bank Accounts Application
info.app.version=1.0.0

# Enable the service to shutdown gracefully
# Expose this endpoint to acutator
endpoints.shutdown.enabled=true
management.endpoint.shutdown.enabled=true
```

<br>

3. Start the `configserver`, then `eurkekaserver`, thehn `accounts` > Go to `localhost:8080/acutator/info` and you will see the properties that you have set as `info` endpoints in `accounts` `application.properties`.

4. Go to `localhost:8070` > you should see your account instnace is up!

5. *Do the same for `cards` and `loans` microservices.*

    - Add **Eureka Discovery Client** (not OpenFeign) dependency to pom.xml
    - Add acutator end points + eureka config info to `application.properties`
  
6. run config > eureka > all microservices. Additioanlly you can navigate to `localhost:8070/eureka/apps/__(service)__` to view info about that instance
    > Additionally you can change hte request header to Accepts "application/json" in potman

7. Similarly, you can **Deregister** instances by calling it's port + actuator + shutdown + `llocalhost:9000/actuator/shutdown`

<br>

## Send Heartbeats from Sevice to `Eurekaserver`
Run all apps, kill the eureka server (you will see that all microservicers are trying to send a heartbeat every 30 seconds but are unable to find an ative Discovery service.)

<br>

## Use Feign Client to invoke other Microservices
This is how microservices use Eureka data and Netlfix Open Feign Client to communicate with other microservices.  You must follow client-side load balacning if possible. Feign Client allows microservices to talk with eachother without knowing exact location details of eachother.

*We will build a new API path inside `accounts` which will be exposed to UI application. This `myCustomerDetails` path will provide a single view of all cards, loans, and accounts. It will use Open Feign Client to invoke cards and loans*.

1. **Open Feign** dependency is only in `accounts` app because it will be invoking info from the other services

2. Insert `@EnableFeignClients` annotation ontop of `AccountsApplication` class:

<br>

```java
@SpringBootApplication
@EnableFeignClients
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

}
```

<br>

3. In `accoutns` create a new package called `com.revature.accounts.service.client` > Here we will make 2 classes: `CardsFeignClient` & `LoansFeignClient`. These are interfaces
    > We use these to invoke `cards` & `loans` busines logic within `accounts`:

<br>

`CardsFeignClient.java`

```java
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

```

<br>

LoansFeignCLient.java

```java
@FeignClient("loans")
public interface LoansFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = "myLoans", consumes = "application/json")
	List<Loans> getLoansDetails(@RequestBody Customer customer);
}
```

<br>

4. Go to accounts controller. Autowire both `FeignClient` interfaces to the controller.

5. Write a method that exposes an API path called `myCUstomerDetails` which passes a CUstomer as the Request Body and invokes both `cards` and `loans` client to return information. Your `AccountsController` should now look like this:

<br>

```java
@RestController
public class AccountsController {

	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	AccountsServiceConfig accountsConfig;

	@Autowired
	LoansFeignClient loansFeignClient;

	@Autowired
	CardsFeignClient cardsFeignClient;

	/**
	 * Passes customer object as parameter in HTTP Request body and returns Account
	 * object based on account found by that cusomter's ID.
	 */
	@PostMapping("/myAccount")
	public Accounts getAccountDetails(@RequestBody Customer customer) {

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (accounts != null) {
			return accounts;
		} else {
			return null;
		}
	}

	/**
	 * This method will return all properties configured for this service from the
	 * auto-wired AccountsServiceConfig in JSON format to the client.
	 */
	@GetMapping("/account/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
				accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}
	
	/**
	 * Passes Customer object as localhost:8080/myCustomerDetails.
	 * Customer obj is passed as method to both cards and loans controllers
	 * by using FeignClient to invoke other microservices and return details.
	 */
	@PostMapping("/myCustomerDetails")
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
}
```

<br>

*It is the responsibility of Eureka Server to find the `loans` and `cards` > It will get instance detials of loans and cards when we send the first request and cache locally.  It will also do load balancing through Spring Cloud LoadBalancing*

5. Start configserver > eurekaserver > accounts > cards > loans. With Postman, make a POST request to `localhost:8080/myCustomerDetails` with `{"customerId" : 1}` as the Request Body >  this will return all details.

<br>

## Generate Docker *Images* to Reflect Changes made

1. Go to root folder where all services are present > open terminal. We have to generate a Dockerfile for `accounts` the old way. Open a terminal within `accounts`

2. First we need an `accounts` JAR file. Tell maven you're not unit testing. Run: `mvn clean install -Dmaven.test.skip=true`

3. Run: `docker build . -t sophiagavrila/accounts`.

4. Do the same for `cards` and `loans`: `cd` into `cards` and run `mvn spring-boot:build-image -Dmaven.test.skip=true` > do the same in `loans/`.

5. `configserver` is up to date > we just need to generate a docker image for `eurekaserver` > run: `mvn spring-boot:build-image -Dmaven.test.skip=true`

6. Run `docker images` and do some cleanup to remove old images (`docker rmi <image-id> -f`)

<br>

## Push Latest Docker IMages to DockerHub

1. Push all images with `docker push sophiagavrila/accounts` (repeat for all 4 images - not configserver)

<br>

## Integrate EurekaServer into Docker Compose File

1. Create a `eurekaserivce` service under `configserver`.
2. In the `accounts` service section, add `eurekaserver` as a dependency, and add it's environemnt variable.
3. Add `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/` to all services declared in domcpose file.

`docker-compose.yml` should look like this:

<br>

```yaml
version: "3.8"

services:

  configserver:
    image: sophiagavrila/configserver:latest
    mem_limit: 700m
    ports:
      - "8071:8071"
    networks:
     - bank
     
  eurekaserver:
    image: sophiagavrila/eurekaserver:latest
    mem_limit: 700m
    ports:
      - "8070:8070"
    networks:
     - bank
    depends_on:
      - configserver
    # Incase config server is not started, set a restart policy and try again
    deploy:
      restart_policy:
        condition: on-failure
        delay: 15s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      # Tells docker where the config server location is that we can connect
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/

  accounts:
    image: sophiagavrila/accounts
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - bank
    # Docker Compose will ensure that config is started first 
    depends_on:
      - configserver
      - eurekaserver
    # Deploy configurations delays accounts before it makes requests to configserver 
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    # Here we are overriding application.properties of the service    
    environment:
       SPRING_PROFILES_ACTIVE: default
       # Make sure we connect to configserver even if it is not on localhost
       SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
       # Tell docker where Eureka is so it can register it
       EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
    
  loans:
    image: sophiagavrila/loans
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - bank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
       SPRING_PROFILES_ACTIVE: default
       SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
       EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
    
  cards:
    image: sophiagavrila/cards
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - bank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
       SPRING_PROFILES_ACTIVE: default
       SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
       EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/  
      
networks:
  bank:
```

<br>

4. Do the same to `dev` and `prod` docker-compose files.