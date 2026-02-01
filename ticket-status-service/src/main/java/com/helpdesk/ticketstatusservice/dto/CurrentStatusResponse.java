package com.helpdesk.ticketstatusservice.dto;

import com.helpdesk.ticketstatusservice.model.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CurrentStatusResponse {
  private Long ticketId;
  private TicketStatus currentStatus;
  private String lastUpdatedBy;
  private LocalDateTime lastUpdatedAt;
  private boolean isDefault; // Indicates if this is a default OPEN status
}