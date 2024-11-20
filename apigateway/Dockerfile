FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/apigateway.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]