FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy entire project
COPY . .

# Move backend to clean path without spaces
RUN cp -r "./my project/backend" /backend

# Build from clean path
WORKDIR /backend
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy from clean path (no spaces)
COPY --from=build /backend/target ./target

# Expose port
EXPOSE 8080

# Start server
ENV PORT=8080
CMD ["java", "-cp", "target/classes:target/*", "-Dport=${PORT}", "org.eclipse.jetty.ee10.maven.plugin.MavenWebAppContext"]
