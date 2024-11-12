# Usa l'immagine base di OpenJDK
FROM openjdk:17-jdk-slim

# Imposta la directory di lavoro nel container
WORKDIR /app

# Copia il file JAR generato da Maven nella directory di lavoro del container
COPY target/ticket-service.jar /app/app.jar

# Definisce il comando da eseguire quando il container viene avviato
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Espone la porta 8083
EXPOSE 8083