FROM maven:3.9-eclipse-temurin-17-alpine

WORKDIR /app

# Copy entire project
COPY . .

# Move backend to clean path without spaces
RUN cp -r "./my project/backend" /backend

# Set working directory to backend
WORKDIR /backend

# Download dependencies for faster startup
RUN mvn dependency:go-offline -q || true

# Expose port
EXPOSE 8080

# Start Jetty using Maven plugin with shell form to expand PORT
ENV PORT=8080
CMD mvn jetty:run -Djetty.http.port=$PORT
