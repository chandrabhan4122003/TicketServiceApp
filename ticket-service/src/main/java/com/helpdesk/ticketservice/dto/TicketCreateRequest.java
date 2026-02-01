package com.helpdesk.ticketservice.dto;

import com.helpdesk.ticketservice.model.IssueCategory;
import com.helpdesk.ticketservice.model.Priority;
import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class TicketCreateRequest {

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be a positive number")
    private Long employeeId;

    @NotBlank(message = "Employee name is required")
    @Size(min = 2, max = 100, message = "Employee name must be between 2 and 100 characters")
    private String employeeName;

    @NotNull(message = "Issue category is required. Valid values: LAPTOP, NETWORK, SOFTWARE, ACCESS")
    private IssueCategory issueCategory;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Priority is required. Valid values: LOW, MEDIUM, HIGH")
    private Priority priority;
}