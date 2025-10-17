# Build stage
FROM gradle:8.4-jdk17 AS build
WORKDIR /workspace/app

# Download dependencies
COPY build.gradle settings.gradle* ./
RUN gradle dependencies --no-daemon

# Build application
COPY src ./src
RUN gradle build --no-daemon -x test

# Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Add health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Set environment variables
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Expose ports
EXPOSE 8080
# Debug port
EXPOSE 5005

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
