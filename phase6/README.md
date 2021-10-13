Phase 6: Routing & Cross Cutting Concerns with Spring Cloud Gateway
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

1. Open postman, send a POST request with the body as `{ "customerId" : 1 }` to `http://localhost:8072/ACCOUNTS/myAccount` > Gateway interacted with eureka and understood that that was directed to `accounts` > the `accounts` microservice was invoked and returned a response.

2. Notice you had to send that request to a URL including letters IN ALL CAPS.  TO change this add the following line to `gatewayserver`'s `application.properties` to allow lowercase letters in the URL to route to the service that is expected to deliver the response.

<br>

```yaml
spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true
```

<br>

##### *But this seemes arbitrary, why add Gateway Server as an extra layer to our services?*
Gateway Server is powerful in the fact that it handles cross-cutting concerns like logging and authentication within our Microservice ecosystem.  We'll get to that in just a bit:

<br>

## Implementing Custom Routing with Spring Cloud Gateway
