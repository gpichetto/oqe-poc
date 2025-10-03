# PDF Generation Service

A Spring Boot microservice for generating PDFs from JSON data.

## Prerequisites

- Docker
- Java 17+
- Gradle (optional, for local development)

## Quick Start with Docker

1. **Make the script executable** (if not already):
   ```bash
   chmod +x run-docker.sh
   ```

2. **Run the service** (uses default API key):
   ```bash
   ./run-docker.sh
   ```

   Or with a custom API key:
   ```bash
   APP_API_KEY="your-custom-api-key" ./run-docker.sh
   ```

3. **Access the service**:
   - API: `http://localhost:8081/api/pdf/render`
   - Health check: `http://localhost:8081/actuator/health`

## API Usage

### Generate PDF

```bash
curl -X POST http://localhost:8081/api/pdf/render \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: your-api-key" \
  -d '[{"title":"Test Item","description":"Test Description","completed":false}]' \
  --output output.pdf
```

### Health Check

```bash
curl http://localhost:8081/actuator/health
```

## Environment Variables

- `APP_API_KEY`: API key for authentication (default: included in script)
- `SPRING_PROFILES_ACTIVE`: Spring profile (default: none)

## Stopping the Service

```bash
docker stop pdf-service
docker rm pdf-service
```

## Local Development

1. Build and run tests:
   ```bash
   ./gradlew build
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```

## Building the Docker Image Manually

```bash
docker build -t pdf-service .
```
