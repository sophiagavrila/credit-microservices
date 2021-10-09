# Credit Microservices
A Spring Boot based microservices application deployed on AWS cloud, containerized and orchestrated with Docker &amp; Kubernetes. Simulated to model the appropriate deconstruction of a banking credit/loan accounting monolith.

<br>

## How To Use This Guide:
This guide is broken into several modules for learning Kubernetes:

- **Phase 1: Understanding the Spring Boot Services** :brain:

<<<<<<< HEAD
- **Phase 2: How to Build, Deploy, and Scale Microservices using Docker** :whale:

- **Phase 3: Configuration Management with Cloud Config** :cloud:

- **Phase 4: Service Discovery & Registration with Eureka and Feign Client** :mag:

- **Phase 5: Making Microservices Resilient with Circuit Breakers** :muscle:

- **Phase 6: Routing &  Cross Cutting Concerns with Spring Cloud Gateway** :scissors:

- **Phase 7: Distributed Tracing & Log Aggrigation with Spring Cloud Sleuth, Zipkin, and RabbitMQ** :incoming_ernvelope:

- **Phase 8: Automatic Self-Healing, Scaling & Deployments with Kubernetes** :octopus:

*Finished!* :tada:

=======
<br>

```json
{
    "customerId" : 1
}
```

<br>

By sending the above request body to the aforementioned endpoint, you are hitting `com.revature.loans.controller.LoansController`'s `getLoansDetails()` method which returns the corresponding `Loans` objects correlated to that `customerId` in an HTTP Reponse Body.

You can try sending the same Post Requst body to each of the services' controllers.
>>>>>>> dd3c76528dc47bd0158e48826378d8f0972b1c3b
