package com.helpdesk.ticketstatusservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusSummaryResponse {
    private LocalDate date;
    private Map<String, Long> statusCounts;
    private Long totalTickets;
}