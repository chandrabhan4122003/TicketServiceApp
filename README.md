# IT Helpdesk Ticket System - Simple Microservices

A beginner-friendly microservices project for managing IT helpdesk tickets, built with Spring Boot. Perfect for learning microservices basics!

## What You'll Learn

- How to build two independent Spring Boot services
- Basic REST API development
- Database operations with JPA and H2
- Simple microservices communication
- Clean code structure with layers (Controller → Service → Repository → Model)

## Project Structure

This project has **two separate Spring Boot applications**:

1. **ticket-service** (Port 8080) - Creates and manages tickets
2. **ticket-status-service** (Port 8081) - Tracks ticket status changes

## What You Need

- Java 17 or higher
- Maven 3.6+
- Any IDE (IntelliJ IDEA, Eclipse, VS Code)

## How to Run

### Option 1: Using Command Line
```bash
# Terminal 1 - Start Ticket Service
cd ticket-service
mvn spring-boot:run

# Terminal 2 - Start Status Service  
cd ticket-status-service
mvn spring-boot:run
```

### Option 2: Using Batch Files (Windows)
- Double-click `run-ticket-service.bat`
- Double-click `run-ticket-status-service.bat`

## Testing Your APIs

### Using Swagger UI (Recommended for Beginners)
- Ticket Service: http://localhost:8080/swagger-ui.html
- Status Service: http://localhost:8081/swagger-ui.html

### Using Database Console
- Ticket Service DB: http://localhost:8080/h2-console
- Status Service DB: http://localhost:8081/h2-console
- Username: `sa`, Password: `password`

## Quick Test

1. Create a ticket using Swagger UI at http://localhost:8080/swagger-ui.html
2. Update its status using Swagger UI at http://localhost:8081/swagger-ui.html
3. Check the database to see your data!

## Learning Resources

- Check `api-samples.md` for example API calls
- Look at `PROJECT-STRUCTURE.md` to understand the code organization
- Explore the code - it's kept simple and well-commented!