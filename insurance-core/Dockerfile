# 1. Usar una imagen ligera de Java 21 (o la versión que estés usando)
FROM eclipse-temurin:21-jdk-alpine

# 2. Configurar el directorio de trabajo
WORKDIR /app

# 3. Copiar el archivo .jar compilado por Maven desde la carpeta target
# Nota: Ajusta el nombre del archivo si tu pom.xml genera uno diferente
COPY target/insurance-core-0.0.1-SNAPSHOT.jar app.jar

# 4. Exponer el puerto 8080 para acceder a la aplicación y al frontend
EXPOSE 8080

# 5. Comando para ejecutar la aplicación de Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
