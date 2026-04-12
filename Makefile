# Scanly SB-Kasse - System Orchestration

.PHONY: help up down logs restart status ps

# Default target: show help
help:
	@echo "Scanly SB-Kasse Management Commands:"
	@echo "------------------------------------"
	@echo "make up      - Build and start all services in detached mode"
	@echo "make down    - Stop and remove all containers"
	@echo "make restart - Restart all services"
	@echo "make logs    - Follow logs from all containers"
	@echo "make ps      - Show status of all containers"
	@echo "------------------------------------"

# Start the system
up:
	@echo "🚀 Starting Scanly System..."
	docker compose up --build -d
	@echo ""
	@echo "✅ System is starting up!"
	@echo "--------------------------------------------------"
	@echo "🌍 Frontend:    http://localhost"
	@echo "⚙️  Backend API: http://localhost:8080/api"
	@echo "📊 Database:    localhost:5432"
	@echo "--------------------------------------------------"
	@echo "Run 'make logs' to see the output."

# Stop the system
down:
	@echo "🛑 Stopping Scanly System..."
	docker compose down

# Restart the system
restart: down up

# Show logs
logs:
	docker compose logs -f

# Show process status
ps status:
	docker compose ps
