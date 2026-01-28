package com.helpdesk.ticketstatusservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private Long ticketId;
    private Long employeeId;
    private String employeeName;
    private String issueCategory;
    private String description;
    private String priority;
    private LocalDateTime createdAt;
}