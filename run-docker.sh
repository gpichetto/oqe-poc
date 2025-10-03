#!/bin/bash

# Configuration
IMAGE_NAME="pdf-service"
CONTAINER_NAME="pdf-service"
PORT=8081
DEBUG_PORT=5006
# Default API key from your .env file
API_KEY="${APP_API_KEY:-nu58PFSz5PEHkoHCkzGvdSDtZ6j1t5x3QBUUd46BaXs=}"


# Colors for output
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check if Docker is installed
if ! command_exists docker; then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

# Build the Docker image
echo -e "${GREEN}Building Docker image...${NC}"
docker build -t $IMAGE_NAME .

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

# Check if container already exists and remove it
if docker ps -a --format '{{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo -e "${GREEN}Removing existing container...${NC}"
    docker rm -f $CONTAINER_NAME >/dev/null
fi

# Run the container
echo -e "${GREEN}Starting container...${NC}"
docker run -d \
    --name $CONTAINER_NAME \
    -p $PORT:8085 \
    $DEBUG_OPTS \
    -e APP_API_KEY="$API_KEY" \
    $IMAGE_NAME

# Show container status
echo -e "\n${GREEN}Container is running!${NC}"
echo -e "\n${GREEN}Container Info:${NC}"
docker ps --filter "name=$CONTAINER_NAME" --format "table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}"

# Show logs
echo -e "\n${GREEN}Tailing logs (Ctrl+C to stop):${NC}"
docker logs -f $CONTAINER_NAME
