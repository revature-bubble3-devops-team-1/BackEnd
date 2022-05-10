FROM openjdk:8-jdk-alpine as runner
# Copy the JAR from the target folder into the container
COPY --from=builder target/Bubble.jar Bubble.jar 
EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/Bubble.jar"]
