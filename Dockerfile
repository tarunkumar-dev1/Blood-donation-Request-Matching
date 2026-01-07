FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /build

# Copy entire project
COPY . .

# Navigate to backend
WORKDIR /build/my project/backend

# Build application
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy entire target directory (WORKDIR from build stage is already set)
COPY --from=build /build/my\ project/backend/target ./target

# Expose port
EXPOSE 8080

# Start server
ENV PORT=8080
CMD ["java", "-cp", "target/classes:target/*", "-Dport=${PORT}", "org.eclipse.jetty.ee10.maven.plugin.MavenWebAppContext"]
