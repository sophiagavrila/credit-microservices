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

## Make changes to the services to connect to Eureka
Each microservice can register itself to Eureka service discovery and send a heartbeat.

1. Start with `accounts` > open `pom.xml` and add the following dependencies:
    - Eureka Discovery Client
    - OpenFeign

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




