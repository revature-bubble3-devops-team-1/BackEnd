<<<<<<< HEAD
=======
# FROM openjdk:8-jdk-alpine
# COPY /target/Bubble.jar Bubble.jar 
# EXPOSE 5000
# ENTRYPOINT ["java", "-jar", "/Bubble.jar"]
>>>>>>> 96c606566c7329c37505e829a48cfa420c615528
FROM maven:3.6.3-openjdk-8 as builder
COPY src/ src/
COPY pom.xml pom.xml
RUN mvn clean package -Dmaven.test.skip

FROM openjdk:8-jdk-alpine as runner
# Copy the JAR from the target folder into the container
COPY --from=builder target/Bubble.jar Bubble.jar 
EXPOSE 5000
<<<<<<< HEAD
ENV DB_URL=jdbc:postgresql://localhost:5432/postgres
ENV DB_USER=postgres
ENV DB_PASS=pass
=======
>>>>>>> 96c606566c7329c37505e829a48cfa420c615528

ENTRYPOINT ["java", "-jar", "/Bubble.jar"]
