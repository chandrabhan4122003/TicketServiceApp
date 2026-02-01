package com.helpdesk.ticketstatusservice.controller;

import com.helpdesk.ticketstatusservice.dto.CurrentStatusResponse;
import com.helpdesk.ticketstatusservice.dto.StatusHistoryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusSummaryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusUpdateRequest;
import com.helpdesk.ticketstatusservice.service.TicketStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
@Validated
public class TicketStatusController {

    private final TicketStatusService ticketStatusService;

    @PostMapping("/update")
    public StatusHistoryResponse updateTicketStatus(@Valid @RequestBody StatusUpdateRequest request) {
        return ticketStatusService.updateTicketStatus(request);
    }

    @GetMapping("/{ticketId}")
    public CurrentStatusResponse getCurrentStatus(
            @PathVariable @Positive(message = "Ticket ID must be a positive number") Long ticketId) {
        return ticketStatusService.getCurrentStatus(ticketId);
    }

    @GetMapping("/{ticketId}/history")
    public List<StatusHistoryResponse> getStatusHistory(
            @PathVariable @Positive(message = "Ticket ID must be a positive number") Long ticketId) {
        return ticketStatusService.getStatusHistory(ticketId);
    }

    @GetMapping("/summary/{date}")
    public StatusSummaryResponse getStatusSummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ticketStatusService.getStatusSummary(date);
    }

    @GetMapping("/all")
    public List<CurrentStatusResponse> getAllTicketsCurrentStatus() {
        return ticketStatusService.getAllTicketsCurrentStatus();
    }

    @GetMapping("")
    public List<CurrentStatusResponse> getAllTicketsStatus() {
        return ticketStatusService.getAllTicketsCurrentStatus();
    }
}