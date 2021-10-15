# Phase 8: Monitoring Metrics & Health with Prometheus and Grafana
There is a goldmine of information about each of our microservices that is avialable at endpoints provided to us by Spring Boot Actuator.  With the use of Micrometer, we can create custom metrics as well, and format all of the data so that it can be read by Prometheus.  Prometheus will read this data an display it in a UI.

## Setup Micrometer Inside Microservices

1.  Inside the 3 microservices `cards`, `accounts`, and `cards` add the following dependencies:
    - **Micrometer** - This will collect data from our Actuator endpoints and format it for Prometheus to scrape.
    - **Prometheus** - Will feed on the data from Micrometer and allow us to query/visualize this data.
        - *Eventually we will use Grafana to build a rich UI dashboard of this data*
    - Make sure you have **AOP** dependency within `accounts` as well.  This is because we will deliver customm metrics from accounts in the form of an Aspect.

2. In `accounts` `AccountsApplication` class add the following `@Bean`. Paste the following:

<br>

```java
    /**
     * We want to expose custom metrics related to how 
     * long it takes this service to process a request.
     * MeterRegistry comes from micrometer dependency.
    */
	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
	    return new TimedAspect(registry);
	}
```

<br>

3. Now go to your `AccountsController` and add a `@Timed` annotation above your `getAccountDetails()` method like so:

<br>

```java
@PostMapping("/myAccount")
// Add it here - We have names, and described the metric to help us understand how
// long it takes to receive a request and return a response.  This configures & exposes a new endpoint.
@Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")
public Accounts getAccountDetails(@RequestBody Customer customer) {

    Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
    if (accounts != null) {
        return accounts;
    } else {
        return null;
    }
}
```

<br>

3. If you run configserver > erurekaserver > and then accounts, navigate to `localhost:8080/actuator/metrics` where you will see all types of metrics regarding this endpoint like `/system.cpu.count` or `process.uptime` for example.  This is a cumnbersome process to monitor each and every one with just actuator....

> This is why we have prepared the endpoints with Micrometer into the format that **Prometheus** will expect. (Go to `localhost:8080/actuator/prometheus`).  This is the end point that will be requested by Prometheus.  This is why we added the dependency in the URL.

<br>

## Implement Prometheus

1. In `accounts/docker-compose`, generate a new folder called `monitoring`.  Make two files in here:
    - `docker-compose.yml`
    - `prometheus.yml`

It is not the responsibility of your MSA to push the data into Prometheus.  It is prometheus' job to PULL the data.

2. Add this into your `prometheus.yml` file:

<br>

```yaml
global:
  scrape_interval:     5s # Set the scrape interval to every 5 seconds.
  evaluation_interval: 5s # Evaluate rules every 5 seconds.
scrape_configs:
  - job_name: 'accounts'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['accounts:8080']
  - job_name: 'loans'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['loans:8090']
  - job_name: 'cards'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['cards:9000']
```

<br>

3. Add this into your `docker-compose.yml` file:

<br>

```yaml
version: "3.8"

services:

  prometheus:
   image: prom/prometheus:latest
   ports:
      - "9090:9090"
   volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
   networks:
    - bank
   
  zipkin:
    image: openzipkin/zipkin
    mem_limit: 700m
    ports:
      - "9411:9411"
    networks:
     - bank

  configserver:
    image: sophiagavrila/configserver:latest
    mem_limit: 700m
    ports:
      - "8071:8071"
    networks:
     - bank
    depends_on:
      - zipkin
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      
  eurekaserver:
    image: sophiagavrila/eurekaserver:latest
    mem_limit: 700m
    ports:
      - "8070:8070"
    networks:
     - bank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 15s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      SPRING_ZIPKIN_BASEURL: http://zipkin:9411/

  accounts:
    image: sophiagavrila/accounts:latest
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - bank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
  
  loans:
    image: sophiagavrila/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - bank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
    
  cards:
    image: sophiagavrila/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - bank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      
  gatewayserver:
    image: sophiagavrila/gatewayserver:latest
    mem_limit: 700m
    ports:
      - "8072:8072"
    networks:
      - bank
    depends_on:
      - configserver
      - eurekaserver
      - cards
      - loans
      - accounts
    deploy:
      restart_policy:
        condition: on-failure
        delay: 45s
        max_attempts: 3
        window: 180s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      SPRING_ZIPKIN_BASEURL: http://zipkin:9411/

networks:
  bank:
```

<br>

4. Now open a terminal within `accounts/docker-compose/monitoring` and run `docker compose up`

5. In Postman make a GET call to `localhost:8080/myAccount` > then check your actuator URL at `localhost:8080/actuator/prometheus` > ctrl + f to find: "getAccountDetails" and you will find the time that it took to process that request.

6. Now navaigate to `localhost:9090/targets` which will expose the Prometheus UI which shows you all instances and the prometheus endpoint URLs.  You can also look at "Graph" to find out things like `system_cpu_usage` which easily shows all of the data scraped.  If you try to send multiple requests, you'll see the cpu increase.

> You can also set up alerts, and status.

7. In a separate terminal, you can kill a container like your `cards` service, return to Prometheus UI and check its status.

> *We will now integrate Grafana to expand on this dashboard.*