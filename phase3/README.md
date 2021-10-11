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

### :one: **Native Approach:** Read the properties from a classpath location

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
<hr>
<br>

### :two: **Read from File System Approach:**
*You may not want to push all of your config properties to GitHub as a part of your application.  Instead you can store the configuration properties in a filesystem on the server that your `configserver` is being, or within a cloud storage like AWS S3 bucket.*

- Save your `config` folder to somewhere in your disk like: `C://config`

- In your `application.properties`, comment out the previouse location and add this search location: `#spring.cloud.config.server.native.search-locations=file:///C://config`

<br>
<hr>
<br>

### :three: :star: **Read from GitHub Repository Approach:** *Standard Approach*

- Create a repository on github containing all of the `.properties` files within your `config` folder like [this](https://github.com/sophiagavrila/credit-microservices-config)

- In `application.properties` of `configserver` change `spring.profiles.active=native` to `spring.profiles.active=git`

- Comment out the previous `spring.cloud.config.server.native.search-locations=file:///C://config` and change it to `spring.cloud.config.server.git.uri=<your-repo>`.

- Tell Cloud Server to clone your repo so it has access to all the properties with: `spring.cloud.config.server.git.clone-on-start=true`

- Tel Cloud Server which branch all of your files are stored at with: `spring.cloud.config.server.git.default-label=main`

*For example, your `application.properties` should look like this:*

```yaml
# This helps us identify microservices
spring.application.name=configserver

# tell spring cloud that we're reading from a git repository
spring.profiles.active=git

# From GitHub repository
spring.cloud.config.server.git.uri=https://github.com/sophiagavrila/credit-microservices-config.git
spring.cloud.config.server.git.clone-on-start=true
spring.cloud.config.server.git.default-label=main

# The port where our config server app will run
server.port=8071
```

<br>

# Update Microservices to Read Properties from Config Server

1. Go to `accounts` services' `pom.xml` > under the `<properties>` tag add the Spring CLoud version (which is copied from your `configserver`'s spring cloud version)

<br>

```xml
<properties>
    <java.version>1.8</java.version>
    <spring-cloud.version>2020.0.4</spring-cloud.version>
</properties>
```

<br>

1b. Add `Cloud Config Client` as a dependency to the `pom.xml`.

<br>  

2. Create the dependency tags below the `</dependency>` tag in the bottom like so (aslo copied from `configserver`):

<br>

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

<br>

3. Go to `accounts` `application.properties` file and tell the app where it can fetch properties from Config Server by adding tho following properties:

<br>

```yaml
# Pointing to the name of service you're configuring
spring.application.name=accounts
# Select the environment profile
spring.profiles.active=prod
# Fetch from the configserver service running on port 8071
spring.config.import=optional:configserver:http://localhost:8071
# optional: indicates that is config server is down, you should still start up accounts service
```

<br>

4. **Add the `AccountsServiceConfig` class:** Write the code to load all properties from config location

   - In `accounts`, create a new package & class: `com.revature.accounts.config.AccountsServiceConfig` 
   - Within it, write the following code:

<br>

```java
/**
 * @ConfigurationProperties reads all properties that begin with "accounts"
 */
@Configuration
@ConfigurationProperties(prefix = "accounts")
@Getter @Setter @ToString
public class AccountsServiceConfig {

	/**
	 * Store all properties fetches in an object
	 */
	private String msg;
	private String buildVersion;
	private Map<String, String> mailDetails;
	private List<String> activeBranches;

}
```

<br>

*The `@ConfigurationProperties` annotation will prompt you to add `spring-boot-configuration-processor` to your `pom.xml`. Add it.*

<br>

5. **Add *`@AutoWired`* `AccountsServiceConfig` and  `getPropertyDetails()` method to `accounts` Controller**
> Your `com.revature.accounts.controller.AccountsController.java` should now look like this: 

<br>

```java
@RestController
public class AccountsController {

	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	AccountsServiceConfig accountsConfig;

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
}
```

</br>


## 6. Start the applications and test that `accounts` has fetched the properties from `configserver`

- Run `configserver` first.

- Then run `accounts`.

- Navigate to `http://localhost:8080/account/properties` and view the properties details returned from `configserver`!

<br>

#### *Follow the same steps for `loans` and `cards` services*

- Add Spring Cloud Config version to pom.xml
  
- Add DependecniesManagement to pom.xml
  
- Add Cloud Config Client dependency
  
- Add Config import properties to application.properties
  
- Add ____ServiceConfig Class
  
- Autowire ___ServiceConfig to ____Controller, addget____Properties() method

<br>

## 7. Generate Docker Images for All Services after Config Server Changes

- In `configserver`'s root directory, run `mvn spring-boot:build-image` to build the Docker image

- Open your terminal in `accounts` > run `mvn clean install` > Now that the new JAR is available, run `docker build . -t <your-name>/accounts` > this will generate a new image, overwriting the previous one tagged `:latest` > that one will have the tag `:<none>`

- Delete the image with the `<none>` tag with `docker rmi <first-3-digits-of-image-id>` *for example* `docker rmi 93c`

- Create the Docker images with the Maven command and delete their older versions for `loans` and `cards` - (*add `-f` at the end of the docker command if you need to force removal*)

<br>

## 8. Push all latest Docker Images to Docker Hub

- Run the following command for all 4 images: `docker push <image-name>` - *for example* `docker push sophiagavrila/accounts`

<br>

## 9. Update Docker Compose File to adapt Config Server changes

- Inside `accounts` root directory, create a new folder called `docker-compose`

- Inside of `docker-compose`, create 3 folders: `default`, `dev`, `prod` > create a `docker-compose.yml` file within each one Here we can control all containers based on the type of environment they're running in.

- Go [here](https://github.com/sophiagavrila/credit-microservices/tree/main/phase3/accounts/docker-compose) and copy the contents into each folder's respective `docker-compose.yml` file

- `cd` into `accounts/docker-compose/default` and run the command `docker compose up` -> this will start all of the containers in the default environment.  Note that some of the containers will restart due to the delay properties set in the docker-compose file.

- Test that everything is up and working by navigating to `localhost:9000/cards/properties` and you will see that it has received the default properties from config server.

<br>

*Finished* :tada: