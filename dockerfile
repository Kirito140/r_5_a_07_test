# Utilise une image Java officielle
FROM openjdk:24-jdk-slim

# Définit le répertoire de travail
WORKDIR /app

# Copie le jar généré dans le conteneur
COPY target/*.jar app.jar

# Expose le port utilisé par Spring Boot
EXPOSE 2005

# Commande de lancement de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
