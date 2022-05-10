# FROM openjdk:8-jdk-alpine
# COPY /target/Bubble.jar Bubble.jar 
# EXPOSE 5000
# ENTRYPOINT ["java", "-jar", "/Bubble.jar"]

FROM openjdk:8-jre-alpine as runner

# Copy the JAR from the target folder into the container
COPY target/Bubble.jar Bubble.jar 

RUN apt-get -qq update

RUN apt-get -qq -y install curl

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/Bubble.jar"]
