package com.helpdesk.ticketservice.dto;

import com.helpdesk.ticketservice.model.IssueCategory;
import com.helpdesk.ticketservice.model.Priority;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private Long ticketId;
    private Long employeeId;
    private String employeeName;
    private IssueCategory issueCategory;
    private String description;
    private Priority priority;
    private LocalDateTime createdAt;
}