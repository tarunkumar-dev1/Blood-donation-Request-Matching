FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /build

# Copy entire project
COPY . .

# Navigate to backend
WORKDIR /build/my project/backend

# Build application - this creates target directory with all JARs
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy entire target directory from build
COPY --from=build "/build/my project/backend/target" ./target

# Expose port
EXPOSE 8080

# Start server using the JAR files in target
ENV PORT=8080
CMD ["java", "-cp", "target/classes:target/*", "-Dport=${PORT}", "org.eclipse.jetty.ee10.maven.plugin.MavenWebAppContext"]
