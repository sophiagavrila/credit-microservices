# Phase 2: Build Deploy and Scale Microservices with Docker
With a monolithic application, there is typically one WAR file that you can deploy on one server. With microservices, as the number of services increase, it becomes very difficult to manage and deploy each service. This is where Docker comes in.

If we were deploy each of our services (loans, cards, and accounts) the traditional way, we would need three separate servers (think of three different EC2's that you rent through AWS).  This would be difficult and expensive.

**Hypervisor** was the original solution to this problem, which enabled the dev to run multiple **Virtual Machines** with their own operating systems.  The Hypervisor is in charge of distributing the total amount of RAM and hard disk available across multiple virtual machines. However, this is very resource heavy and expensive. 

**Docker** introduced the concept of **containerization**.  Ontop of the server's physical hardware runs the server's host operating system - be it MacOS, Windows, or Linux.  The Docker Engine is responsible for ditributing and assigning resources as per the demand of the containers. Inside the containers you don't need an operating system either.  You just need the libraries necessary for installing your service.  Since containers can be stopped/restarted with ease and quickly, the end user will not be affected with all these happening in the backend.

<br>

## How do Containers Differ from Virtual Machines?
- Containers don't need a Guest OS

- Containers have their own "isolated environments" which means they don't affect eachother.
  - For example, `loans` service could run on Java 8, `cards` service could run on Java 11, and `accounts` could be coded in Python

- Containers are light weight, easy to maintain, and easy to start/stop.

<br>

## Summary of Docker

**What is a container?** <br>
> A container is a loosely isolated environemtn that allows us to build and run software packages.  These software packages include the dcode and all dependencie to run applications quickly and reliably on any computing environment.  We call these packages container **images**.

<br>

**What is software containerization?**
> Software containerization is an OS virtualization method that is used to deploy and run containers without using a virtual machine (VM). Containers can run on physical hardware, in the cloud, VM's and multiple OSs.

<br>

**What is Docker?**
> Docker is one of the tools that used the idea of the isolated resources to create a set of tools that allows applications to be packaged with all the dependencies installed and ran wherever wanted.







