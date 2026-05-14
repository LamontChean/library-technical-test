FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (for better caching)
COPY pom.xml .
COPY domain/pom.xml domain/
COPY infrastructure/pom.xml infrastructure/
COPY application/pom.xml application/
COPY facade/pom.xml facade/
COPY web/pom.xml web/
COPY bootstrap/pom.xml bootstrap/
COPY test/pom.xml test/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY domain/src domain/src
COPY infrastructure/src infrastructure/src
COPY application/src application/src
COPY facade/src facade/src
COPY web/src web/src
COPY bootstrap/src bootstrap/src
COPY test/src test/src

# Build the application
RUN mvn clean package -Dmaven.test.skip=true -B

# Runtime stage
FROM eclipse-temurin:17

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/bootstrap/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
