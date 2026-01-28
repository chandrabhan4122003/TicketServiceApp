# 🧪 Complete Testing Guide - IT Helpdesk Microservices

This guide will walk you through testing every aspect of the application, including service communication, databases, and all endpoints.

## 🚀 **Step 1: Start Both Services**

### Option A: Using IntelliJ IDEA

1. Open IntelliJ IDEA
2. Import both projects:
   - `ticket-service`
   - `ticket-status-service`
3. Run both main classes:
   - `TicketServiceApplication.java` (Port 8080)
   - `TicketStatusServiceApplication.java` (Port 8081)

### Option B: Using Command Line

```bash
# Terminal 1 - Start Ticket Service
cd ticket-service
mvn spring-boot:run

# Terminal 2 - Start Status Service
cd ticket-status-service
mvn spring-boot:run
```

### ✅ **Verify Services Are Running**

Check these URLs in your browser:

- Ticket Service: http://localhost:8080/swagger-ui.html
- Status Service: http://localhost:8081/swagger-ui.html

If both load successfully, you're ready to test!

---

## 🌐 **Step 2: Access Swagger UI (API Documentation)**

### **Ticket Service Swagger**

**URL:** http://localhost:8080/swagger-ui.html

**Available Endpoints:**

- `POST /tickets/create` - Create new ticket
- `GET /tickets/{ticketId}` - Get ticket by ID
- `GET /tickets/employee/{employeeId}` - Get tickets by employee
- `GET /tickets/priority/{priority}` - Get tickets by priority

### **Status Service Swagger**

**URL:** http://localhost:8081/swagger-ui.html

**Available Endpoints:**

- `POST /status/update` - Update ticket status (with validation)
- `GET /status/{ticketId}` - Get status history for ticket
- `GET /status/summary/{date}` - Get daily status summary

---

## 🗄️ **Step 3: Access H2 Database Consoles**

### **Ticket Service Database**

**URL:** http://localhost:8080/h2-console

**Connection Settings:**

- **JDBC URL:** `jdbc:h2:mem:ticketdb`
- **Username:** `sa`
- **Password:** `password`
- **Driver Class:** `org.h2.Driver`

**Tables to Explore:**

```sql
-- View all tickets
SELECT * FROM tickets;

-- Count tickets by priority
SELECT priority, COUNT(*) FROM tickets GROUP BY priority;
```

### **Status Service Database**

**URL:** http://localhost:8081/h2-console

**Connection Settings:**

- **JDBC URL:** `jdbc:h2:mem:statusdb`
- **Username:** `sa`
- **Password:** `password`
- **Driver Class:** `org.h2.Driver`

**Tables to Explore:**

```sql
-- View all status history
SELECT * FROM ticket_status_history;

-- View status changes for specific ticket
SELECT * FROM ticket_status_history WHERE ticket_id = 1 ORDER BY updated_at;
```

---

## 🧪 **Step 4: Complete Testing Workflow**

### **Test 1: Create Tickets (Ticket Service)**

#### **Using Swagger UI:**

1. Go to http://localhost:8080/swagger-ui.html
2. Click on `POST /tickets/create`
3. Click "Try it out"
4. Use this JSON:

```json
{
  "employeeId": 1001,
  "employeeName": "John Doe",
  "issueCategory": "LAPTOP",
  "description": "Screen keeps flickering and going black",
  "priority": "HIGH"
}
```

5. Click "Execute"
6. **Note the `ticketId` in the response** (e.g., 1)

#### **Expected Response:**

```json
{
  "ticketId": 1,
  "employeeId": 1001,
  "employeeName": "John Doe",
  "issueCategory": "LAPTOP",
  "description": "Screen keeps flickering and going black",
  "priority": "HIGH",
  "createdAt": "2026-01-29T10:30:00"
}
```

#### **Create More Test Tickets:**

```json
{
  "employeeId": 2002,
  "employeeName": "Jane Smith",
  "issueCategory": "NETWORK",
  "description": "Cannot connect to WiFi in conference room",
  "priority": "MEDIUM"
}
```

```json
{
  "employeeId": 3003,
  "employeeName": "Bob Wilson",
  "issueCategory": "SOFTWARE",
  "description": "Excel crashes when opening large files",
  "priority": "LOW"
}
```

### **Test 2: Retrieve Tickets (Ticket Service)**

#### **Get Ticket by ID:**

1. Use `GET /tickets/{ticketId}`
2. Enter ticket ID: `1`
3. Should return the ticket details

#### **Get Tickets by Employee:**

1. Use `GET /tickets/employee/{employeeId}`
2. Enter employee ID: `1001`
3. Should return all tickets for that employee

#### **Get Tickets by Priority:**

1. Use `GET /tickets/priority/{priority}`
2. Enter priority: `HIGH`
3. Should return all high priority tickets

### **Test 3: Update Status with Communication (Status Service)**

#### **✅ Test Valid Ticket ID (Should Work):**

1. Go to http://localhost:8081/swagger-ui.html
2. Click on `POST /status/update`
3. Click "Try it out"
4. Use this JSON (replace `1` with your actual ticket ID):

```json
{
  "ticketId": 1,
  "status": "IN_PROGRESS",
  "updatedBy": "tech@company.com"
}
```

5. Click "Execute"

**What Happens Behind the Scenes:**

```
1. Status Service receives request
2. Makes HTTP call: GET http://localhost:8080/tickets/1
3. Ticket Service responds with ticket data
4. Status Service saves the status update
5. Returns success response
```

**Expected Response:**

```json
{
  "id": 1,
  "ticketId": 1,
  "status": "IN_PROGRESS",
  "updatedBy": "tech@company.com",
  "updatedAt": "2026-01-29T10:35:00"
}
```

#### **❌ Test Invalid Ticket ID (Should Fail):**

1. Use the same endpoint
2. Use this JSON with non-existent ticket ID:

```json
{
  "ticketId": 999,
  "status": "RESOLVED",
  "updatedBy": "tech@company.com"
}
```

**What Happens Behind the Scenes:**

```
1. Status Service receives request
2. Makes HTTP call: GET http://localhost:8080/tickets/999
3. Ticket Service responds with 404 Not Found
4. Status Service throws TicketNotFoundException
5. Returns error response
```

**Expected Error Response:**

```json
{
  "timestamp": "2026-01-29T10:40:00",
  "status": 404,
  "error": "Ticket Not Found",
  "message": "Ticket with ID 999 not found"
}
```

### **Test 4: Status History and Summaries**

#### **Get Status History:**

1. Use `GET /status/{ticketId}`
2. Enter ticket ID: `1`
3. Should return all status changes for that ticket

#### **Get Daily Summary:**

1. Use `GET /status/summary/{date}`
2. Enter date: `2026-01-29`
3. Should return summary of status changes for that day

---

## 🔍 **Step 5: Monitor Communication Logs**

### **Check IntelliJ Console Logs**

**In Status Service Console, look for:**

```
INFO  - Calling ticket service to verify ticket ID: 1
INFO  - Ticket verified: 1 - John Doe
INFO  - Status updated successfully for ticket 1
```

**For failed validation:**

```
ERROR - Ticket not found: Ticket with ID 999 not found
```

---

## 📊 **Step 6: Database Verification**

### **Check Ticket Service Database:**

1. Go to http://localhost:8080/h2-console
2. Connect with settings above
3. Run query:

```sql
SELECT * FROM tickets ORDER BY created_at DESC;
```

### **Check Status Service Database:**

1. Go to http://localhost:8081/h2-console
2. Connect with settings above
3. Run query:

```sql
SELECT
    tsh.ticket_id,
    tsh.status,
    tsh.updated_by,
    tsh.updated_at
FROM ticket_status_history tsh
ORDER BY tsh.updated_at DESC;
```

### **Verify Data Consistency:**

```sql
-- This should show only valid ticket IDs (no orphaned status records)
SELECT DISTINCT ticket_id FROM ticket_status_history;
```

---

## 🎯 **Step 7: Complete Test Scenarios**

### **Scenario A: Normal Workflow**

1. Create ticket → Get ticket ID 1
2. Update status to "IN_PROGRESS" → Success
3. Update status to "RESOLVED" → Success
4. Update status to "CLOSED" → Success
5. Check status history → Shows all changes

### **Scenario B: Error Handling**

1. Try to update status for ticket ID 999 → Get 404 error
2. Try to update status for ticket ID -1 → Get 404 error
3. Verify no orphaned records in status database

### **Scenario C: Multiple Tickets**

1. Create 3 different tickets
2. Update status for each ticket
3. Check daily summary
4. Verify all data in both databases

---

## 🚨 **Troubleshooting**

### **Common Issues:**

#### **Service Won't Start:**

- Check if ports 8080/8081 are already in use
- Verify Java 17+ is installed
- Check for compilation errors in IntelliJ

#### **Swagger UI Not Loading:**

- Verify service is running
- Check correct port number
- Clear browser cache

#### **H2 Console Connection Failed:**

- Verify JDBC URL matches exactly
- Check username/password
- Ensure service is running

#### **Communication Errors:**

- Verify both services are running
- Check network connectivity between services
- Look at console logs for detailed error messages

#### **404 Errors on Valid Tickets:**

- Verify ticket was actually created
- Check ticket ID in database
- Ensure using correct ticket ID in status update

---

## 📈 **Advanced Testing**

### **Using Postman/Curl:**

#### **Create Ticket:**

```bash
curl -X POST http://localhost:8080/tickets/create \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": 4004,
    "employeeName": "Alice Johnson",
    "issueCategory": "ACCESS",
    "description": "Cannot access shared drive",
    "priority": "MEDIUM"
  }'
```

#### **Update Status:**

```bash
curl -X POST http://localhost:8081/status/update \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 1,
    "status": "IN_PROGRESS",
    "updatedBy": "support@company.com"
  }'
```

### **Load Testing:**

Create multiple tickets and status updates to test system behavior under load.

---

## ✅ **Success Criteria**

You've successfully tested the application when:

1. ✅ Both services start without errors
2. ✅ Swagger UI loads for both services
3. ✅ H2 consoles connect successfully
4. ✅ Can create tickets and get valid responses
5. ✅ Can update status for existing tickets
6. ✅ Get proper 404 errors for non-existent tickets
7. ✅ See communication logs in console
8. ✅ Data appears correctly in both databases
9. ✅ No orphaned status records for invalid tickets

**Congratulations! Your microservices are communicating properly! 🎉**
