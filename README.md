# Credit Microservices
A Spring Boot based microservices application deployed on AWS cloud, shipped and orchestrated with Docker &amp; Kubernetes. Simulated to model the appropriate deconstruction of a banking credit/loan accounting monolith.

<br>

## Phase 1:
1.  Clone Microservices application complete with the following Spring Boot services:
  - **Accounts**
  - **Loans**
  - **Cards**

> *Each service has a `data.sql` file that autoinitializes data within the Spring Boot Project so that we can test our endpoints. For ezample, run the **Loans** service as a Spring Boot App, then send a `POST` request to the endpoint `http://localhost:8090/myLoans` in the following format:*

<br>

```json
{
    "customerId" : 1
}
```

<br>

By sending the above request body to the aforementioned endpoint, you are hitting `com.revature.loans.controller.LoansController`'s `getLoansDetails()` method which returns the corresponding `Loans` objects correlated to that `customerId` in an HTTP Reponse Body.

You can try sending the same Post Requst body to each of the services' controllers.
