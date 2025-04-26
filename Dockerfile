FROM openjdk:21-slim

WORKDIR /app

# Установка curl и других инструментов для отладки
RUN apt-get update && apt-get install -y \
    curl \
    vim \
    less \
    procps \
    net-tools \
    && rm -rf /var/lib/apt/lists/*

# Копируем JAR файл
COPY target/*.jar app.jar

EXPOSE 8095

ENTRYPOINT ["java", "-jar", "app.jar"]
