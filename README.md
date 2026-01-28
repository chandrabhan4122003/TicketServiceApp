# IT Helpdesk Ticket System - Microservices with Communication

A comprehensive microservices project for managing IT helpdesk tickets, built with Spring Boot. Perfect for learning microservices architecture and inter-service communication!

## What You'll Learn

- How to build two communicating Spring Boot services
- Basic REST API development
- Database operations with JPA and H2
- **Microservices communication using HTTP/REST**
- **Data validation across services**
- **Error handling in distributed systems**
- Clean code structure with layers (Controller → Service → Repository → Model)
- **WebClient for service-to-service calls**

## Project Structure

This project has **two communicating Spring Boot applications**:

1. **ticket-service** (Port 8080) - Creates and manages tickets
2. **ticket-status-service** (Port 8081) - Tracks ticket status changes with validation

### 🔗 **Service Communication**

- **Status Service validates tickets** by calling Ticket Service via HTTP
- **Data integrity ensured** - cannot update status for non-existent tickets
- **Real-time validation** using WebClient for HTTP communication
- **Proper error handling** with custom exceptions and global error handlers

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

## 🔗 **Communication Testing**

### Test the Service Communication

1. **Create a ticket** using Swagger UI at http://localhost:8080/swagger-ui.html
2. **Note the ticket ID** returned (e.g., 1)
3. **Update status** using the ticket ID at http://localhost:8081/swagger-ui.html
4. **Try with invalid ticket ID** (e.g., 999) - should get error!

### Expected Behavior

- ✅ **Valid ticket ID**: Status update succeeds
- ❌ **Invalid ticket ID**: Returns 404 "Ticket Not Found" error
- 🔍 **Check logs**: Status service logs show HTTP calls to ticket service

### Communication Flow

```
Status Service (8081) → HTTP GET → Ticket Service (8080)
                     ← Validation ←
```

## 🏗️ **Architecture Features**

### What's New in This Version

- **Service-to-Service Communication**: Status service validates tickets via HTTP calls
- **Data Integrity**: Cannot update status for non-existent tickets
- **Error Handling**: Proper HTTP error responses with custom exceptions
- **WebClient Integration**: Modern reactive HTTP client for inter-service calls
- **Logging**: Detailed logs showing communication between services

### Technical Implementation

- **WebClient**: For making HTTP calls between services
- **Custom Exceptions**: `TicketNotFoundException` for proper error handling
- **Global Exception Handler**: Centralized error response formatting
- **DTO Validation**: Cross-service data validation and consistency

## Learning Resources

- **📋 `TESTING-GUIDE.md`** - **Complete step-by-step testing instructions**
- Check `api-samples.md` for example API calls
- Look at `PROJECT-STRUCTURE.md` to understand the code organization
- Explore the code - it's kept simple and well-commented!

## 📚 **Key Learning Points**

### Microservices Concepts

1. **Service Independence**: Each service has its own database and responsibility
2. **Inter-Service Communication**: HTTP-based communication between services
3. **Data Consistency**: Validation across service boundaries
4. **Error Propagation**: How errors flow between services

### Spring Boot Features Used

- **WebClient**: Modern HTTP client for service calls
- **Custom Exceptions**: Domain-specific error handling
- **Global Exception Handler**: Centralized error response management
- **Dependency Injection**: Clean separation of concerns
- **Configuration Classes**: Centralized bean configuration

### Files to Study for Communication

- `TicketServiceClient.java` - HTTP client implementation
- `WebClientConfig.java` - HTTP client configuration
- `TicketStatusService.java` - Service integration logic
- `GlobalExceptionHandler.java` - Error handling patterns
