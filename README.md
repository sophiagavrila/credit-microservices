# Credit Microservices
Despite the many advantages of Microservices there are a fair few drawbacks to distributed architecture including complexity, operational overhead, security, and performance. I have created a nine-phase guide to teach you how to solve these problems through the implementation of the most widely used technologies in the industry. By phase9 you will have fully deployed a microservices application to a Kubernetes GKE cluster, complete with distributed tracing and performance monitoring.  Enjoy!

<br>

## Topics Covered
This guide is broken into several modules for learning Microservices, Telemetry, Docker & Kubernetes:

- [**Phase 1: Understanding the Cloned Spring Boot Microservices**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase1) :brain:

- [**Phase 2: How to Build, Deploy, and Scale Microservices using Docker**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase2) :whale:

- [**Phase 3: Configuration Management with Spring Cloud Config**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase3) :key:

- [**Phase 4: Service Discovery & Registration with Eureka and Feign Client**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase4) :phone:

- [**Phase 5: Making Microservices Resilient with Circuit Breakers**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase5) :muscle:

- [**Phase 6: Routing & Cross Cutting Concerns with Spring Cloud Gateway**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase6) :scissors:

- [**Phase 7: Distributed Tracing & Log Aggrigation with Spring Cloud Sleuth, Zipkin, and RabbitMQ**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase7) :incoming_envelope:

- [**Phase 8: Monitoring Metrics & Health with Prometheus and Grafana**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase8) :chart_with_upwards_trend:

- [**Phase 9: Automatic Self-Healing, Scaling & Deployments with Kubernetes**](https://github.com/sophiagavrila/credit-microservices/tree/main/phase9) :octopus:

<br>

#### *Note:* <br>
The Docker images for all services within this application (except for `accounts`) were generated automatically using [Cloud Native Buildpacks](https://buildpacks.io/). The command used to generate them (in the root directory of each service) is: 

```
mvn spring-boot:build-image -Dmaven.test.skip=true`
```

### Deployment YML's
- Final `docker-compose` files are in `phase9/accounts/docker-compose/<env>`
- Final `docker-compose` files for monitoring are in `phase9/accounts/docker-compose/monitoring` 
- Kubernetes manifests are in [`phase9/accounts/kubernetes`](https://github.com/sophiagavrila/credit-microservices/tree/main/phase9/accounts/kubernetes)


<br>

### *What are Microservices*?
Microservices are a form of service-oriented architecture style wherein applications are built as a collection of different smaller services rather than one whole app.  Microservices architecture breaks apps down into their smallest components, independent from eachother so that they may be easily scaled and shipped.
