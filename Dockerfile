FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy pom.xml and build
COPY "my project/backend/pom.xml" .
RUN mvn dependency:go-offline -q

# Copy source
COPY "my project/backend/src" src/
COPY "my project/backend/target" target/ || true

# Build
RUN mvn clean package -q

# Expose port
EXPOSE 8080

# Start server
ENV PORT=8080
CMD ["java", "-cp", "target/classes:target/dependency/*", "-Dport=8080", "org.eclipse.jetty.ee10.maven.plugin.MavenWebAppContext"]
