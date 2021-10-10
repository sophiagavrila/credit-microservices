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
   - **Spring Boot Actuator**

4. Add the **Buildpack** `<image>` tag to `configserver`'s `pom.xml`, below `</dependencyManagement>` like so:

<br>

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <image>
                    <name>sophia/${project.artifactId}</name>
                </image>
            </configuration>
        </plugin>
    </plugins>
</build>
```

<br>

6. Go to the main Spring Boot applicaiton class `com.revature.configserver.ConfigserverApplication.java` and apply the `@EnableConfigServer` annotation over the `ConfigserverApplication` class like so:

<br>

```java
/**
 * @EnableConfigServer makes this application a ConfigServer which will read
 *                     configuration properties from a centralized repository
 *                     and expose them as REST end-points to your other
 *                     microservices that have the ConfigClient dependency.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigserverApplication.class, args);
	}

}
```

<br>


## Thre are multiple ways to provide `configserver` with properties:
<br>

1. **Native Approach:** Read the properties from a classpath location

   - In this repo, go to `configserver` > `src/main/resources/config` > you will see all applicationproperties stored in `application.properties` files for each service and a specific environemnt (dev, test, prod) >  They're located [here](https://github.com/sophiagavrila/credit-microservices/tree/main/phase3/configserver/src/main/resources/config)

   - Right click on `configserver/src/main/resources` and create a `config` folder to put these.

   - The master `accounts.properties` (within `src/main/resources`) should contain the following properties:

> *In order to tell your `configserver` which properties file to read from , you must configurea that in your master `application.properties` file within `src/main/resources`. Within it, paste the following:

<br>

```yaml
# This helps us identify microservices
spring.application.name=configserver

# native tells configserver that we're reading from file system inside of classpath
spring.profiles.active=native

# Expose to the app where the configurations are located in the app
spring.cloud.config.server.native.search-locations=classpath:/config

# The port where our config server app will run
server.port=8071
```

<br>

- Run the application and in your browser navigate to `http://localhost:8071/accounts/default` > You will see all of the default config properties, just as you would if you tried a different environemnt setting like `http://localhost:8071/accounts/dev`
    > *You can format this text for better readability at [Json Pretty Print](https://jsonformatter.org/json-pretty-print)*

<br>
<br>

2. **Read from File System Approach:**
*You may not want to push all of your config properties to GitHub as a part of your application.  Instead you can store the configuration properties in a filesystem on the server that your `configserver` is being, or within a cloud storage like AWS S3 bucket.*

- Save your `config` folder to somewhere in your disk like: `C://config`
- In your `application.properties`, comment out the previouse location and add this search location: `#spring.cloud.config.server.native.search-locations=file:///C://config`

<br>
<br>

3. **Read from GitHub Repository Approach:**

- Create a repository on github containing all of the `.properties` files within your `config` folder like [this](https://github.com/sophiagavrila/credit-microservices-config)

