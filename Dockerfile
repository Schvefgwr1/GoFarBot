FROM ubuntu:latest
LABEL authors="Сева Счетов"

# Использование базового образа OpenJDK
FROM openjdk:17-jdk-slim

# Установка директории для приложения
WORKDIR /app

# Копируем файл с зависимостями и скомпилированное приложение
COPY target/GoFarBot-0.0.1-SNAPSHOT.jar /app/app.jar

# Указываем, какой порт будет открыт
EXPOSE 8080

# Команда запуска Spring Boot приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
