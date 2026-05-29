# ==========================================
# ETAPA 1: Construcción (Build)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiamos el "Maven Wrapper" y el archivo de dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copiamos todo el código fuente
COPY src src

# Damos permisos de ejecución al wrapper y compilamos el proyecto en la nube
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ==========================================
# ETAPA 2: Ejecución (Run)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiamos ÚNICAMENTE el archivo .jar que se generó mágicamente en la Etapa 1
COPY --from=build /app/target/insurance-core-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto y ejecutamos
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]