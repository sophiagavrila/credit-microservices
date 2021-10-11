# Phase 4: Service Discovery & Registration with Eureka and Feign Client
Service Discovery & Registration deals with solving the problems that can occur when Microservices talk to eachother, i.e perform API calls.

In traditional network topology, applications have static network locations.  Hence IP addresses of relevant external locations can be read from a configuraion file, as these addresses rarely change.

In modern microservices architecture, knowing the right network location of an application is a much more complex problem for the clients as service instances might have dynamically assigned IP addressed.  Moreover, the number may vary due to autoscaling and failures.

Microservice Service Discovery & Registration is a way for applications and microservices to locate eachother on a network.  This includes:
    
- *A central server (or servers) that maintain a global view of addresses*
- *Microservices/clients that connect to the central server to register their address when they start & are ready*.    
- *Microservices/clients need to send their heartbeats at regular intervals to central server to inform on their health.*