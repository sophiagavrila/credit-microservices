# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Information about who maintains the image
MAINTAINER Sophia Gavrila

# Add the application's JAR to the container
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

# Execute the application
ENTRYPOINT ["java", "-jar", "/accounts-0.0.1-SNAPSHOT.jar"]