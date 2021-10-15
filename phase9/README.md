# Phase 9: Automatic Self-Healing, Scaling & Deployments with Kubernetes
Before we get into the specifics of Kubernetes (also called K8s), we have to understand what problem it is solving in modern software.  Before microservices, enterprise applications were deployed as monolith applications on one server.  These old-school monoliths were deployed and maintained *internally*.

In the early 2000's cloud technology became the solution to decreasing infrastructure costs, and mitigating latency to end users.  The relief of not having to maintain physical hardware, and the option of pay-per-use, fueled the widespread adoption of cloud. Amazon Web Services (AWS), Microsoft Azure and Google Cloud are some of the popular Cloud Service Providers available today.

Microservices architecture solved the issues of monoliths in many ways.  Decomposing an application into different smaller services makes it easier to understand, develop and test.  Microservices are more resistant to failures and only a small service has to be rebuilt whenever the code is updated, thus reducing the deployment time. The reduction in deployment time encourages continuous delivery and deployment.  However, an application that comes in many parts can cause deployment issues within the cloud.

<br>

### *How do we automate deployments, rollouts & Rollbacks?*
If you have hundreds of microservices running at once, it can be difficult to stop, update, and start their containers when there are new versions of that service. Kubernetes provides a rollout mechanism that updates the running services to the newer version of the software.  This prevents any down time, allowing the new docker image to be integrated smoothly into the ecosystem.

### *How do you introduce automated self-healing?*
In the case that a service has something wrong with it, Kubernetes will automatically detect the failure and rollout a replacement container.

### How do we auto scale our services
Kubernetes automates the process of monitoring our services and scaling them out (creating more instances) based on metrics like CPU utilization, etc.

<br>

## What is Kubernetes? (K8s)
Kubernetes is an open-source system for automating deployment, scaling, and managing containerized applications.  It is the most famous orchestration platform and it is cloud neutral.

- The name "Kubernetes" originates from Greek meaning helmsman or pilot.  K8s as an abbreviation results from counting the eight letters between the "K" and "S".

- Google open-sourced the Kubernetes project in 2014. K8s combines over 15 years of Google's experience running production workloads at scale.

- Kubernetes privides you with a framework to run distributed systems systems resiliently. It takes care of scaling and failover for your application, provides deployment patterns, and more.  It provides you with:
    - *Service discovery and load balancing*
    - *Storage orchestration*
    - *Automated rollouts and rollbacks*
    - *Automatic bin packing*
      - Automatic bin packing: You provide Kubernetes with a cluster of nodes that it can use to run containerized tasks. You tell Kubernetes how much CPU and memory (RAM) each container needs. Kubernetes can fit containers onto your nodes to make the best use of your resources.
    - *Self-healing*
    - Secret and configuration management*
