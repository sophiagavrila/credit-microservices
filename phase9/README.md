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

<br>

## Internal Architecture of Kubernetes
Kubernetes will handle your cluster of nodes within your Microservices architecture.
> ***What is a cluster?*** A cluster is a collection of various servers working together to achieve something.<br>
> In any cluster there is a **master node** and **worker nodes**: the *worker nodes* handle client requests, *master nodes* take care of making sure that all worker nodes are running properly. 

<br>

#### The **Master Node** is responsible for checking on the status of the worker nodes. It uses 4 components to achieve this (all togethere they're called Control Panel):
    
1. **kube API Server**: the Master Node exposes all of its operations with kube API server.  Kube API server is like a *gateway* into your cluster (and gatekeeper that only allows authenticated users/systems)
        - *How do we interact with the cluster*? There is a UI and a CLI (called `kubectl`).  You can *only* interact with the Master Node.  You can tell it to create another container, scale down, etc.

2. **Scheduler** takes calls via kubectl to instantiate/replicate containers (instances). Scheduler also checks the worker nodes based on who can take on another load, and the container is deployed there.

3.  **Controller Manager** checks on the health of the containers and worker nodes and ensures that *desired* state and *current* state are always matching.

4. **etcd** is a consistent and highly-available key value store used as Kubernetes' backing store for all cluster data.  Think of it as a database for your cluster.  It even retains information such as how many instance syou require for one service.

<br>

#### Worker nodes manage handing the request from the user and delivering a response. It has 4 important components:
*There can be any number of work nodes based on the traffic you recieve or number of requests received.  Think of a Worker Node as a jumbo server that houses mini servers like pods.*

1. **kubelet** is an agent that runs within every worker node and establishes communication with the Master Node.  For example, if a worker node is set to run another container, scheduler will send the request to kubelet.

2. **Docker** is installed on every worker node.

3. **kube-proxy** will help you to expose all your containers' endpoint urls to your end users.  It bypasses firewall restrictions and allows your public endpoints to be requested, redirecting user requests to the appropraite container. End users only interact with kube-proxy

4. **pod**  represents a group of one or more application containers, and some shared resources for those containers. Those resources include: Shared storage; as Volumes, Networking; as a unique cluster IP address, Information about how to run each container, such as the container image version or specific ports to use.
    > Each pod has an IP address which forwards teh address of the containers and can be accessed via kube-proxy.

*You can learn more about the above topics [here](https://kubernetes.io/docs/tutorials/kubernetes-basics/explore/explore-intro/)*
