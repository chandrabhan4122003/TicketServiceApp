package com.helpdesk.ticketstatusservice.dto;

import com.helpdesk.ticketstatusservice.model.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusHistoryResponse {
    private Long id;
    private Long ticketId;
    private TicketStatus status;
    private String updatedBy;
    private LocalDateTime updatedAt;
}