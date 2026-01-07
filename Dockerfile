FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /build

# Copy entire project
COPY . .

# Build from backend directory
WORKDIR /build/my project/backend
RUN mvn clean package -DskipTests -q && \
    mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy built artifacts from build stage
COPY --from=build /build/my project/backend/target/classes ./target/classes
COPY --from=build /build/my project/backend/target/dependency ./target/dependency

# Expose port
EXPOSE 8080

# Start server
ENV PORT=8080
CMD ["java", "-cp", "target/classes:target/dependency/*", "-Dport=${PORT}", "org.eclipse.jetty.ee10.maven.plugin.MavenWebAppContext"]
