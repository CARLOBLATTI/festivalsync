FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/gateway.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]