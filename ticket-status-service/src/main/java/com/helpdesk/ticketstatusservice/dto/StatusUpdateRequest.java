package com.helpdesk.ticketstatusservice.dto;

import com.helpdesk.ticketstatusservice.model.TicketStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private Long ticketId;
    private TicketStatus status;
    private String updatedBy;
}