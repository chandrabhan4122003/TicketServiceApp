package com.helpdesk.ticketstatusservice.controller;

import com.helpdesk.ticketstatusservice.dto.StatusHistoryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusSummaryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusUpdateRequest;
import com.helpdesk.ticketstatusservice.service.TicketStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class TicketStatusController {
    
    private final TicketStatusService ticketStatusService;
    
    @PostMapping("/update")
    public StatusHistoryResponse updateTicketStatus(@RequestBody StatusUpdateRequest request) {
        return ticketStatusService.updateTicketStatus(request);
    }
    
    @GetMapping("/{ticketId}")
    public List<StatusHistoryResponse> getStatusHistory(@PathVariable Long ticketId) {
        return ticketStatusService.getStatusHistory(ticketId);
    }
    
    @GetMapping("/summary/{date}")
    public StatusSummaryResponse getStatusSummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ticketStatusService.getStatusSummary(date);
    }
}