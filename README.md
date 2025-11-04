# PDF Generation Service

A Spring Boot microservice for generating PDFs from JSON data.

## Prerequisites

- Docker
- Java 17+
- Gradle

## Run Options

### Local (from IDE)

- Run the Spring Boot application (port is `8085` by default per `src/main/resources/application.properties`).

### Docker Compose (direct)

You can run the stack without the helper script:

```bash
# Build the image
docker compose build pdf-service

# Start services (Redis + app). You can set APP_API_KEY inline if desired.
docker compose up -d

# View logs
docker compose logs -f

# Stop and remove containers
docker compose down
```

### Helper Script (wraps Docker Compose)

1. Make the script executable:
   ```bash
   chmod +x run-docker.sh
   ```
2. Run the service:
   ```bash
   ./run-docker.sh
   ```

## Access URLs

- Local run (IDE, port 8085):
  - Swagger UI: `http://localhost:8085/swagger-ui/index.html`
  - OpenAPI JSON: `http://localhost:8085/v3/api-docs`
  - Health: `http://localhost:8085/actuator/health`
- Docker/Script (host port 8081 -> app 8085):
  - Swagger UI: `http://localhost:8081/swagger-ui/index.html`
  - OpenAPI JSON: `http://localhost:8081/v3/api-docs`
  - Health: `http://localhost:8081/actuator/health`

## API Usage

### Endpoint

- **Path**: `POST /api/pdf/render-job-ticket-short-work-period`
- **Consumes**: `multipart/form-data`
- **Produces**: `application/pdf` or `application/json`
- **Security**: API key header `X-API-KEY`

### Request parts

- **jobTicket** (required): JSON string or file with job ticket data
- **shortWorkPeriod** (optional): JSON string or file with short work period details

### Example: Download PDF response

```bash
# Docker: use 8081. Local IDE: use 8085.
curl -X POST "http://localhost:8081/api/pdf/render-job-ticket-short-work-period" \
  -H "X-API-KEY: your-api-key" \
  -H "Accept: application/pdf" \
  -F "jobTicket=@jobTicket.json;type=application/json" \
  -F "shortWorkPeriod=@shortWorkPeriod.json;type=application/json" \
  --output job-ticket.pdf
```

### Example: Get Base64 PDF in JSON response

```bash
# Docker: use 8081. Local IDE: use 8085.
curl -X POST "http://localhost:8081/api/pdf/render-job-ticket-short-work-period" \
  -H "X-API-KEY: your-api-key" \
  -H "Accept: application/json" \
  -F "jobTicket=@jobTicket.json;type=application/json"
```

### OpenAPI / Swagger

- Refer to Access URLs above for the correct port depending on how you run the app.

### Health Check

```bash
# Docker: 8081, Local IDE: 8085
curl http://localhost:8081/actuator/health
```
### Environment Variables

- `APP_API_KEY`: API key for authentication (default: included in script)
- `SPRING_PROFILES_ACTIVE`: Spring profile (e.g., `dev`, `production`). Docker Compose runs with `dev`.

### Stopping the Service

```bash
docker stop pdf-service
docker rm pdf-service
```
### Building the Docker Image Manually

```bash
docker build -t pdf-service .
```