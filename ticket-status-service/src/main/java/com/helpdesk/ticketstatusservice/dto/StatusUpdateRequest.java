package com.helpdesk.ticketstatusservice.dto;

import com.helpdesk.ticketstatusservice.model.TicketStatus;
import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "Ticket ID is required")
    @Positive(message = "Ticket ID must be a positive number (no leading zeros allowed)")
    private Long ticketId;

    @NotNull(message = "Status is required. Valid values: OPEN, IN_PROGRESS, RESOLVED, CLOSED")
    private TicketStatus status;

    @NotBlank(message = "Updated by field is required (who is making this change)")
    @Size(min = 3, max = 100, message = "Updated by must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9@._-]+$", message = "Updated by can only contain letters, numbers, @, ., _, -")
    private String updatedBy;
}