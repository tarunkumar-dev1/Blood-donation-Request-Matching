FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml
COPY my\ project/backend/pom.xml ./pom.xml

# Download dependencies
RUN mvn dependency:go-offline -q

# Copy source code
COPY my\ project/backend/src ./src

# Build application
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy built artifacts from build stage
COPY --from=build /app/target/classes ./target/classes
COPY --from=build /app/target/dependency ./target/dependency

# Expose port
EXPOSE 8080

# Start server
ENV PORT=8080
CMD ["java", "-cp", "target/classes:target/dependency/*", "-Dport=${PORT}", "org.eclipse.jetty.ee10.maven.plugin.MavenWebAppContext"]
