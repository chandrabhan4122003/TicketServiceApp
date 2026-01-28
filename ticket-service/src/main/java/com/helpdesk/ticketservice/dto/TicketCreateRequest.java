package com.helpdesk.ticketservice.dto;

import com.helpdesk.ticketservice.model.IssueCategory;
import com.helpdesk.ticketservice.model.Priority;
import lombok.Data;

@Data
public class TicketCreateRequest {
    private Long employeeId;
    private String employeeName;
    private IssueCategory issueCategory;
    private String description;
    private Priority priority;
}