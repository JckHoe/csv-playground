.PHONY: build-go build-spring up down clean

# Build Go application
build-go:
	cd mark1 && docker build -t csv-playground-go .

# Build Spring Boot application
build-spring:
	cd mark2 && docker build -t csv-playground-spring .

# Build all applications
build: build-go build-spring

# Start all services
up:
	docker-compose up -d

# Stop all services
down:
	docker-compose down

# Clean up Docker resources
clean:
	docker-compose down -v
	docker rmi csv-playground-go csv-playground-spring || true

# Show logs
logs:
	docker-compose logs -f

# Restart all services
restart: down up

# Help command
help:
	@echo "Available commands:"
	@echo "  make build-go     - Build Go application"
	@echo "  make build-spring - Build Spring Boot application"
	@echo "  make build        - Build all applications"
	@echo "  make up          - Start all services"
	@echo "  make down        - Stop all services"
	@echo "  make clean       - Clean up Docker resources"
	@echo "  make logs        - Show logs"
	@echo "  make restart     - Restart all services" 