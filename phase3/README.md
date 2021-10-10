# Phase 3: Configuration Management with CLoud Config
As outlined in the 12 Factors of Cloud-Native Apps, environment-specifc configuration *must* be stored independently from your code.  Never add embedded configurations to your souce code; instead, maintain your configuration completely separated from your deployable microservice.  If we keep the configuration packaged within the microservice, we'll need to redeploy each of the hundred instanced to make the change.

Configuration Management within Microservices poses 3 questions:

1. How do we separate the configurations/properties from teh microservices so that the same Docker image can be deployed in mutliple environemnts?

2. How do we inject configurations/properties that microservices need during the start up of the service?

3. How do we maintain configurations/properties in a centralized repository along with versioning of them?

<br>

### Spring CLoud Config :cloud:
Spring Cloud Config provides server and client-side support for externalized configuration in a distributed system.  With the Config Server, you have a central place to manage external properties for applications across all environments. 