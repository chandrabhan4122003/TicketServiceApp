# API Examples - Simple IT Helpdesk System

## Getting Started

The easiest way to test these APIs is using **Swagger UI**:
- Ticket Service: http://localhost:8080/swagger-ui.html  
- Status Service: http://localhost:8081/swagger-ui.html

Just click on an API, click "Try it out", fill in the data, and hit "Execute"!

## Ticket Service APIs (Port 8080)

### 1. Create a New Ticket
**URL:** `POST http://localhost:8080/tickets/create`

**Request Body:**
```json
{
  "employeeId": 1001,
  "employeeName": "John Doe",
  "issueCategory": "LAPTOP",
  "description": "My laptop screen keeps flickering",
  "priority": "HIGH"
}
```

**What you'll get back:**
```json
{
  "ticketId": 1,
  "employeeId": 1001,
  "employeeName": "John Doe",
  "issueCategory": "LAPTOP",
  "description": "My laptop screen keeps flickering",
  "priority": "HIGH",
  "createdAt": "2026-01-27T10:30:00"
}
```

### 2. Get a Ticket by ID
**URL:** `GET http://localhost:8080/tickets/1`

### 3. Get All Tickets for an Employee
**URL:** `GET http://localhost:8080/tickets/employee/1001`

### 4. Get All High Priority Tickets
**URL:** `GET http://localhost:8080/tickets/priority/HIGH`



## Status Service APIs (Port 8081)

### 1. Update Ticket Status
**URL:** `POST http://localhost:8081/status/update`

**Request Body:**
```json
{
  "ticketId": 1,
  "status": "IN_PROGRESS",
  "updatedBy": "support@company.com"
}
```

### 2. See All Status Changes for a Ticket
**URL:** `GET http://localhost:8081/status/1`

### 3. Get Daily Summary
**URL:** `GET http://localhost:8081/status/summary/2026-01-27`



## Complete Example Workflow

### Step 1: Create a ticket
Use Swagger UI or:
```bash
curl -X POST http://localhost:8080/tickets/create \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 2001,
    "employeeName": "Jane Smith",
    "issueCategory": "NETWORK",
    "description": "Cannot connect to WiFi",
    "priority": "MEDIUM"
  }'
```

### Step 2: Update status to "In Progress"
```bash
curl -X POST http://localhost:8081/status/update \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 1,
    "status": "IN_PROGRESS",
    "updatedBy": "tech@company.com"
  }'
```

### Step 3: Mark as resolved
```bash
curl -X POST http://localhost:8081/status/update \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 1,
    "status": "RESOLVED",
    "updatedBy": "tech@company.com"
  }'
```

## Valid Values

### Issue Categories
- `LAPTOP` - Laptop/hardware issues
- `NETWORK` - Internet/network problems  
- `SOFTWARE` - Software installation/bugs
- `ACCESS` - Login/permission issues

### Priorities
- `LOW` - Can wait
- `MEDIUM` - Normal priority
- `HIGH` - Urgent

### Ticket Statuses
- `OPEN` - Just created
- `IN_PROGRESS` - Someone is working on it
- `RESOLVED` - Fixed but not closed
- `CLOSED` - Completely done

## Tips for Beginners

1. **Start with Swagger UI** - It's much easier than writing curl commands
2. **Check the H2 database** - Go to http://localhost:8080/h2-console to see your data
3. **Create a ticket first** - You need a ticket before you can update its status
4. **Try different scenarios** - Create multiple tickets, update statuses, see what happens!

## Common Mistakes to Avoid

- Don't forget to start both services
- Make sure you use the right port (8080 for tickets, 8081 for status)
- Ticket ID is auto-generated, don't try to set it manually
- Status updates need an existing ticket ID