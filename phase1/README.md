# Phase 1: Understanding Each Service

1.  Clone [this Microservices application](https://github.com/sophiagavrila/credit-microservices-template) complete with the following Spring Boot services.  I reccomend distributing the template through [GitHub Classroom](https://classroom.github.com/):
  - **Accounts**
  - **Loans**
  - **Cards**

> *Each service has a `data.sql` file that autoinitializes data within the Spring Boot Project so that we can test our endpoints and retrieve pre-inserted objects.* 
> *To test the functionality of each service, send a `POST` request from Postman to that the service's respective port and controller's end point*

For example, run the **Loans** service as a Spring Boot App, then send a `POST` request to the endpoint `http://localhost:8090/myLoans` in the following format:*

```json
{
    "customerId" : 1
}
```

You will hit `com.revature.loans.controller.LoansController`'s `getLoansDetails()` method which returns the corresponding `Loans` objects correlated to that `customerId` in an HTTP Reponse Body.
