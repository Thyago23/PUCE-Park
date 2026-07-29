FROM gradle:8.6.0-jdk17 AS builder
WORKDIR /app

# Copiamos el código fuente
COPY . .

# Compilamos la aplicación omitiendo las pruebas
RUN gradle build -x test --no-daemon

# Fase de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el JAR generado desde la etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
