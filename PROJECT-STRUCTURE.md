# Project Structure - Simple Version

```
IT-Helpdesk-Ticket-System/
├── README.md                           # How to run the project
├── api-samples.md                      # API examples and testing guide
├── PROJECT-STRUCTURE.md               # This file
├── run-ticket-service.bat             # Easy way to start ticket service
├── run-ticket-status-service.bat      # Easy way to start status service
├── ticket-service/                    # First microservice
│   ├── pom.xml                        # Dependencies and build config
│   └── src/main/
│       ├── java/com/helpdesk/ticketservice/
│       │   ├── TicketServiceApplication.java    # Main class to start the app
│       │   ├── controller/
│       │   │   └── TicketController.java        # REST API endpoints
│       │   ├── service/
│       │   │   └── TicketService.java           # Business logic
│       │   ├── repository/
│       │   │   └── TicketRepository.java        # Database operations
│       │   ├── model/
│       │   │   ├── Ticket.java                  # Database table structure
│       │   │   ├── IssueCategory.java           # LAPTOP, NETWORK, etc.
│       │   │   └── Priority.java                # LOW, MEDIUM, HIGH
│       │   └── dto/
│       │       ├── TicketCreateRequest.java     # Input for creating tickets
│       │       └── TicketResponse.java          # Output when returning tickets
│       └── resources/
│           └── application.yml                  # Configuration (port, database)
└── ticket-status-service/             # Second microservice
    ├── pom.xml                        # Dependencies and build config
    └── src/main/
        ├── java/com/helpdesk/ticketstatusservice/
        │   ├── TicketStatusServiceApplication.java  # Main class to start the app
        │   ├── controller/
        │   │   └── TicketStatusController.java      # REST API endpoints
        │   ├── service/
        │   │   └── TicketStatusService.java         # Business logic
        │   ├── repository/
        │   │   └── TicketStatusHistoryRepository.java # Database operations
        │   ├── model/
        │   │   ├── TicketStatusHistory.java         # Database table structure
        │   │   └── TicketStatus.java                # OPEN, IN_PROGRESS, etc.
        │   └── dto/
        │       ├── StatusUpdateRequest.java         # Input for status updates
        │       ├── StatusHistoryResponse.java       # Output for status history
        │       └── StatusSummaryResponse.java       # Output for daily summaries
        └── resources/
            └── application.yml                      # Configuration (port, database)
```

## Understanding the Structure

### Why Two Separate Projects?
- **ticket-service** handles creating and reading tickets
- **ticket-status-service** handles status updates and history
- They run independently on different ports (8080 and 8081)
- This is the **microservices** approach - separate services for separate responsibilities

### What Each Layer Does

#### 1. **Controller Layer** (`@RestController`)
- Handles HTTP requests (GET, POST)
- Defines API endpoints like `/tickets/create`
- Calls the service layer to do the actual work

#### 2. **Service Layer** (`@Service`)
- Contains business logic
- Processes data and makes decisions
- Calls the repository layer to save/retrieve data

#### 3. **Repository Layer** (`@Repository`)
- Talks to the database
- Provides methods like `findById()`, `save()`
- Uses Spring Data JPA for easy database operations

#### 4. **Model Layer** (`@Entity`)
- Defines database table structure
- Maps Java objects to database tables
- Contains the actual data fields

#### 5. **DTO Layer** (Data Transfer Objects)
- Defines what data goes in and out of APIs
- Separates internal data structure from API structure
- Makes APIs cleaner and more secure

## Key Features (Simplified)

### What We Kept Simple
- ✅ Basic Spring Boot setup
- ✅ Simple REST controllers
- ✅ Easy-to-understand service methods
- ✅ Straightforward database operations
- ✅ Clean separation of concerns
- ✅ H2 database (no complex setup needed)
- ✅ Swagger UI for easy testing

### What We Removed for Beginners
- ❌ Complex error handling
- ❌ Input validation annotations
- ❌ Professional logging
- ❌ Advanced HTTP status codes
- ❌ Complex exception classes
- ❌ Enterprise-level configurations

## Learning Path

1. **Start with Models** - Understand `Ticket.java` and `TicketStatusHistory.java`
2. **Look at Controllers** - See how APIs are defined
3. **Understand Services** - See where business logic lives
4. **Check Repositories** - See how data is saved/retrieved
5. **Run and Test** - Use Swagger UI to see everything in action!

This structure follows **Spring Boot best practices** while keeping things simple enough for beginners to understand and learn from.