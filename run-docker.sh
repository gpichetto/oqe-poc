#!/bin/bash

# Configuration
IMAGE_NAME="pdf-service"
CONTAINER_NAME="pdf-service"
REDIS_CONTAINER="redis-oqe"
PORT=8081
DEBUG_PORT=5006
NETWORK_NAME="pdf-network"
# Default API key from your .env file
API_KEY="${APP_API_KEY:-nu58PFSz5PEHkoHCkzGvdSDtZ6j1t5x3QBUUd46BaXs=}"
REDIS_PORT=6379

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to create network if it doesn't exist
create_network() {
    if ! docker network inspect $NETWORK_NAME &>/dev/null; then
        echo -e "${YELLOW}Creating network $NETWORK_NAME...${NC}"
        docker network create $NETWORK_NAME
    fi
}

# Check if Docker is installed
if ! command_exists docker; then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

# Create network if it doesn't exist
create_network

# Check if Redis is running, if not start it
if ! docker ps | grep -q $REDIS_CONTAINER; then
    echo -e "${YELLOW}Redis container not found. Starting Redis...${NC}"
    if ! docker compose up -d redis-oqe; then
        echo -e "${RED}Failed to start Redis. Make sure docker compose is installed.${NC}"
        exit 1
    fi
    echo -e "${GREEN}Redis started successfully on port ${REDIS_PORT}${NC}"
    # Give Redis a moment to initialize
    sleep 5
fi

# Build the Docker image
echo -e "${GREEN}Building Docker image...${NC}"
docker compose build --no-cache pdf-service

# Check if debug mode is enabled
if [ "$1" = "--debug" ] || [ "$1" = "-d" ]; then
    DEBUG_OPTS="-e JAVA_TOOL_OPTIONS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006' -p $DEBUG_PORT:5006"
    echo -e "${GREEN}Debug mode enabled on port $DEBUG_PORT${NC}"
fi

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Error: Failed to build Docker image"
    exit 1
fi

# Stop and remove existing container if it exists
if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo -e "${YELLOW}Stopping and removing existing container...${NC}"
    docker stop $CONTAINER_NAME >/dev/null 2>&1
    docker rm $CONTAINER_NAME >/dev/null 2>&1
fi

# Use docker compose to start the services
echo -e "${GREEN}Starting PDF Service with Redis...${NC}"
if ! docker compose up -d pdf-service; then
    echo -e "${RED}Failed to start PDF Service. Check the logs with 'docker compose logs -f'${NC}"
    exit 1
fi

# Show logs
sleep 2
echo -e "\n${GREEN}Containers started successfully!${NC}"
echo -e "PDF Service: http://localhost:${PORT}/actuator/health"
echo -e "Redis: redis://localhost:${REDIS_PORT}"
echo -e "\n${YELLOW}To view logs, run: docker compose logs -f${NC}"
echo -e "${YELLOW}To stop the services, run: docker compose down${NC}"
