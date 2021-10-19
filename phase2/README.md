# Phase 2: Build Deploy and Scale Microservices with Docker
With a monolithic application, there is typically one WAR file that you can deploy on one server. With microservices, as the number of services increases, it becomes very difficult to manage and deploy each service. This is where Docker comes in.

If we were deploy each of our services (loans, cards, and accounts) the traditional way, we would need three separate servers (think of three different EC2's that you rent through AWS).  This would be difficult and expensive.

**Hypervisor** was the original solution to this problem, which enabled the dev to run multiple **Virtual Machines** with their own operating systems.  The Hypervisor is in charge of distributing the total amount of RAM and hard disk available across multiple virtual machines. However, this is very resource heavy and expensive. 

**Docker** introduced the concept of **containerization**.  Ontop of the server's physical hardware runs the server's host operating system - be it MacOS, Windows, or Linux.  Ontop of the host OS is the Docker Engine which is responsible for ditributing and assigning resources as per the demand of the containers. Inside the containers you don't need an operating system either.  You just need the libraries necessary for installing your service.  Since containers can be stopped/restarted with ease and quickly, the end user will not be affected with all these happening in the backend.

<br>

## How do Containers Differ from Virtual Machines?
- Containers don't need a Guest OS

- Containers are light weight, easy to maintain, and easy to start/stop.

- Containers still have their own "isolated environments" which means they don't affect eachother, **similar to virtual machines**.
  - For example, `loans` service could run on Java 8, `cards` service could run on Java 11, and `accounts` could be coded in Python


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

<br>

# Dockerize the Exisiting Services
We will generate a `Dockerfile` for each existing service.  Spring Documentation has great articles like [this](https://spring.io/guides/topicals/spring-boot-docker/) on how to Dockerize a Spring Boot applcation, but you can go ahead and follow the steps below.

1. Right click on the root directory of your `accounts` app > create a new file named `Dockerfile` and paste the following within it:

<br>

```Dockerfile
# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Information about who maintains the image
MAINTAINER Sophia Gavrila

# Add the application's JAR to the container
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# Execute the application
ENTRYPOINT ["java", "-jar", "/accounts-0.0.1-SNAPSHOT.jar"]
```

<br>

> :exclamation: *Before we build an image make sure that you have [Docker Desktop](https://www.docker.com/products/docker-desktop) downloaded.  Then click on its executable icon and make sure the Docker engine is running on your computer before continuing.*

2. First build the JAR file with maven so Docker can copy it into the container. In the root directory of your project open a terminal and run `mvn clean install`. This will generate `accounts-0.0.1-SNAPSHOT` in the `target/` directory of your project.

3. To build an **image** from the Dockerfile within the `accounts` application, open a terminal in the root of the project and run:

<br>

```
docker build . -t sophiagavrila/accounts
```

<br>

4. **Run the Container**: run `docker run -d -p 8080:8080 sophia/accounts`
> Here we are exposing port 8080 (to be accessed from the "outside world") to our container's port 8080. Docker ports follow this pattern - `HOST : CONTAINER`

The above command will start the container in "detached mode" due to the `-d`.

5. View the containers running with `docker ps`.

6. Open Postman and send the same POST request to `http://localhost:8080/myAccount`.  This will be processed by the application running within the Docker container.

7. Back in the terminal, you can run a duplicate container (and instance of your application) by running:

<br>

```
docker run -d -p 8081:8080 sophia/accounts
```

<br>

> Here you are allowing the second container to be accessed via port 8081 on your local machine.  Send the same POST request to ``http://localhost:8081/myAccount` and you will receive a response by the second instance of your application running in container #2 with it's port 8080 exposed at **8081**.

Deploying this application is now exceptionally easy and we can increase the number of applicaiton instances as user traffic grows.

### More Docker Commands

<br>


|     Docker Command       |     Description          |
| ------------- | ------------- |
| "docker build . -t eazybytes/accounts" | To generate a docker image based on a Dockerfile |
| "docker run  -p 8081:8080 eazybytes/accounts" | To start a docker container based on a given image |
| "docker images" | To list all the docker images present in the Docker server |
| "docker image inspect image-id" | To display detailed image information for a given image id |
| "docker image rm image-id" | To remove one or more images for a given image ids |
| "docker image push docker.io/eazybytes/accounts" | To push an image or a repository to a registry |
| "docker image pull docker.io/eazybytes/accounts" | To pull an image or a repository from a registry |
| "docker ps" | To show all running containers |
| "docker ps -a" | To show all containers including running and stopped |
| "docker container start container-id" | To start one or more stopped containers |
| "docker container pause container-id" | To pause all processes within one or more containers |
| "docker container unpause container-id" | To unpause all processes within one or more containers |
| "docker container stop container-id" | To stop one or more running containers |
| "docker container kill container-id" | To kill one or more running containers instantly |
| "docker container restart container-id" | To restart one or more containers |
| "docker container inspect container-id" | To inspect all the details for a given container id |
| "docker container logs container-id" | To fetch the logs of a given container id |
| "docker container logs -f container-id" | To follow log output of a given container id |
| "docker container rm container-id" | To remove one or more containers based on container ids |
| "docker container prune" | To remove all stopped containers |
| "docker compose up" | To create and start containers based on given docker compose file |
| "docker compose stop" | To stop services |

<br>

## Using Buildpacks to Generate Dockerfile for Loans Microservice
Generating hundreds of Dockerfiles can become tedious, and takes a lot of expertise. Fortunately there is a way to automate this process called **Buildpacks**.

A **buildpack** is a program that turns source code into a runnable container image. Usually, buildpacks encapsulate a single language ecosystem toolchain. There are buildpacks for Ruby, Go, NodeJs, Java, Python, and more. Read more about Dockerfiles vs. Buildpacks [here](https://technology.doximity.com/articles/buildpacks-vs-dockerfiles).

1. Open the `pom.xml` file of `loans` > under the `</dependencies>` tag, add the following `<image>` tag and its nested elements within the `build` tag. *If the maven plugin and lombok tag isnt' there, add them in as well)*

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
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>
```

<br>

2. Right click on the `loans` app root directory and run: `mvn spring-boot:build-image`.
   > Docker will try to pull the images that correspond to out dependencies and code.

3. If you run `docker images` you'll see that your image has been created!  Now you can run it: `docker run -d -p 8090:8090 sophia/loans`.

4. Generate a Dockerfile for `cards` as well > paste the same `<image>` tag into the `pom.xml` file into `cards` as you did for `loans` > run `mvn spring-boot:build-image`

<br>

## Pushing and Pulling from Dockerhub
Dockerhub is a place for you to push up or pull down custom images - it's like GitHub for Docker Images.  This is useful if you need to share your code and replicate its deployment environment, like if you were to ship your software to a testing team, that team could pull down the custom image generated for your software.

1. Log into hub.docker.com - create an account if you haven't already.

2. Go to Repositories > Click *Create repository*.

3. After your username, you have the option to name it *ie* `loans` -> [username]/loans > make it public > click Create.

4. Go back to your terminal > run `docker login` > enter your info

5. Rename your image to match the name of your repository: run `docker tag <old-image-name> username/loans`

*For example:*

```
docker tag sophia/loans sophiagavrila/loans
```

6. Push it to your remote repository: run `docker push username/loans`

Now go back to hub.docker.com and you'll see your newly created repository. Similarly, you can pull images with `docker pull`.

7. Push both your `cards` and `accounts` images.  You can also push images by renaming (tagging) them to your repository name `/` image tag like so:

```
docker image tag not-my-repo-name/cards sophiagavrila/cards

docker push sophiagavrila/cards
```

<br>

<br>

## Intro to Docker Compose
So far we have manually started each of the containers one by one, for all three services.  As applications scale, this gets harder to manage manually. That's where Docker Compose comes in; with a single command, it allows multiple containers to start. Docker Compose is a tool that is used to manage all containers via one command and a `docker-compose.yml` file.  By default, we have Docker Compose installed in our local system by way of downloading Docker Desktop (to check run, `docker-compose --version`)


1. We will run the `accounts` service first out of all the microservices, so it will house our `docker-compose.yml` file.  Right click on the `accounts` project and create a new file called `docker-compose.yml` > paste the following code into it:


<br>

```yml
# This is the version of Docker Compose we're using https://docs.docker.com/compose/compose-file/
version: "3.8"

# This is a command will queue the following containers to run
services:

  # Accounts is the first service we want to run
  accounts:
    # This is the public dockerhub repository name of the image to run a container from
    image: sophiagavrila/accounts:latest
    # Limiting accounts service to using only 700mb
    mem_limit: 700m
    # Exposing port 8080 to the host machine, connecting to 8080 within the container
    ports:
      - "8080:8080"
    networks:
      # specifying the common network shared among services mentioned at the bottom
      - bank-network
    
  loans:
    image: sophiagavrila/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - bank-network
    
  cards:
    image: sophiagavrila/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - bank-network
# Network is a root element which can be declared like services    
networks:
  bank-network:
```

<br>

2. `cd` into your `accounts` app root directory and run: `docker compose up`.
    > This will begin all of your services within their respective containers at once.

    > If you run `docker compose up -d` instead, you will be able to interact with the terminal.

3. Run `docker compose stop` to terminate all the containers that are running.
