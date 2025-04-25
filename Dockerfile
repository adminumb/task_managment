FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Установка curl
RUN apk update && apk add --no-cache curl

COPY target/*.jar app.jar

EXPOSE 8095

ENTRYPOINT ["java", "-jar", "app.jar"]
