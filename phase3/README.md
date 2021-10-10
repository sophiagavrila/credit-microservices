# Phase 3: Configuration Management with Cloud Config
As outlined in the [12 Factors of Cloud-Native Apps](https://12factor.net/), environment-specifc configuration *must* be stored independently from your code.  Never add embedded configurations to your souce code; instead, maintain your configuration completely separated from your deployable microservice.  If we keep the configuration packaged within the microservice, we'll need to redeploy each of the hundred instanced to make the change.

Configuration Management within Microservices poses 3 questions:

1. How do we separate the configurations/properties from teh microservices so that the same Docker image can be deployed in mutliple environemnts?

2. How do we inject configurations/properties that microservices need during the start up of the service?

3. How do we maintain configurations/properties in a centralized repository along with versioning of them?

<br>

## Spring Cloud Config :cloud:
[**Spring Cloud Config**](https://github.com/sophiagavrila/credit-microservices.git) provides server and client-side support for externalized configuration in a distributed system.  With the Config Server, you have a central place to manage external properties for applications across all environments. 

<br>

#### Spring Cloud Config Server Features
- HTTP, resource-based API for external configuration (name-value pairs, or equivelent YAML content)

- Encrypt and decrypt property values

- Embeddable easily in a Spring Boot application using `@EnableConfigServer`

<br>

#### Config Client features (for Microservices)
- Bind to the Config Server and initialize Spring Environment with remote property sources

- Encrypt and decrypt property values

<br>

# Create a Service that will Act as a Cloud Config Server 
We will create a Spring Boot application will act as a Config Server where it will read all of your properties from a centralized repository so that it can be used by all of your individual microservices (`accounts`, `loans`, & `cards`).

1. In the same IDE where your other microservices are go to File > New > Spring Starter Project

2. Name it `configserver` > Maven, Java 8, Jar > Group: `com.revature` > Package: `com.revature.configserver` > Next

3. Add the following dependencies:
   - **Config Server** 
        > ***Config Client*** *is the dependency that will be added to each individual service that fetches from our `configserver`**.