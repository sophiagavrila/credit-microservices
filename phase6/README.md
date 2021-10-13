Phase 6: Routing & Cross Cutting Concerns with Spring Cloud Gateway
Spring Cloud Gateway is a library for building an API gateway, so it looks like any other Spring Boot application.  If you're a Spring developer, you'll find it's very easy to get started with Spring Cloud Gateway with just a few lines of code.

**Spring Cloud Gateway** is intended to sit between a requester and a resource that's being requested, where it intercepts, analuzes, and modifies every request.  That means you can route requests based on their context.  Did a request include a header indicating an API version?  We can route that request to the appropriately versioned backend.  Does the reuqest require sticky sessions?  The gateway can keep track of each user's session.

Spring Cloud Gateway is a replacement of **Zuul** for the following reasons:
    - Spring Cloud Gateway is the preferred API gateway implementation from the Spring Cloud Team.  It's built on Spring 5, Reactor, and Spring WebFlux.  Not only that, it also includes circuit breaker integration & service disvoery with Eureka.
    - Spring Cloud Gateway is non-blocking in nature. 
    - Spring Cloud Gateway is regarded as having superior performance to that of Zuul. 

<br>

## Build a Spring Cloud Gateway Service

1. Create a new Spring Boot starter project > 