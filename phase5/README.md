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


