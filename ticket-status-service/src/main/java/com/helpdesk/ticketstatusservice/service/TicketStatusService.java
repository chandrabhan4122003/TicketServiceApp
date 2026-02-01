package com.helpdesk.ticketstatusservice.service;

import com.helpdesk.ticketstatusservice.client.TicketServiceClient;
import com.helpdesk.ticketstatusservice.dto.CurrentStatusResponse;
import com.helpdesk.ticketstatusservice.dto.StatusHistoryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusSummaryResponse;
import com.helpdesk.ticketstatusservice.dto.StatusUpdateRequest;
import com.helpdesk.ticketstatusservice.dto.TicketResponse;
import com.helpdesk.ticketstatusservice.model.TicketStatus;
import com.helpdesk.ticketstatusservice.model.TicketStatusHistory;
import com.helpdesk.ticketstatusservice.repository.TicketStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketStatusService {

    private final TicketStatusHistoryRepository statusHistoryRepository;
    private final TicketServiceClient ticketServiceClient;

    public StatusHistoryResponse updateTicketStatus(StatusUpdateRequest request) {
        log.info("Updating status for ticket ID: {} to {}", request.getTicketId(), request.getStatus());

        // 1. Verify ticket exists by calling ticket service
        TicketResponse ticket = ticketServiceClient.getTicketById(request.getTicketId());
        log.info("Ticket verified: {} - {}", ticket.getTicketId(), ticket.getEmployeeName());

        // 2. Create status history entry
        TicketStatusHistory statusHistory = new TicketStatusHistory();
        statusHistory.setTicketId(request.getTicketId());
        statusHistory.setStatus(request.getStatus());
        statusHistory.setUpdatedBy(request.getUpdatedBy());

        // 3. Save status update
        TicketStatusHistory savedHistory = statusHistoryRepository.save(statusHistory);
        log.info("Status updated successfully for ticket {}", request.getTicketId());

        return mapToResponse(savedHistory);
    }

    public CurrentStatusResponse getCurrentStatus(Long ticketId) {
        log.info("Getting current status for ticket ID: {}", ticketId);

        // Verify ticket exists before returning status
        TicketResponse ticket = ticketServiceClient.getTicketById(ticketId);

        // Get the most recent status for this ticket
        List<TicketStatusHistory> history = statusHistoryRepository.findByTicketIdOrderByUpdatedAtDesc(ticketId);

        CurrentStatusResponse response = new CurrentStatusResponse();
        response.setTicketId(ticketId);

        if (history.isEmpty()) {
            // No status history exists, return default "OPEN" status
            log.info("No status history found for ticket {}, returning default OPEN status", ticketId);
            response.setCurrentStatus(TicketStatus.OPEN);
            response.setLastUpdatedBy("system");
            response.setLastUpdatedAt(ticket.getCreatedAt()); // Use ticket creation time
            response.setDefault(true); // Indicate this is a default status
        } else {
            // Return the most recent (current) status
            TicketStatusHistory currentStatus = history.get(0); // First item is most recent due to DESC order
            response.setCurrentStatus(currentStatus.getStatus());
            response.setLastUpdatedBy(currentStatus.getUpdatedBy());
            response.setLastUpdatedAt(currentStatus.getUpdatedAt());
            response.setDefault(false); // This is an actual status record
        }

        return response;
    }

    public List<StatusHistoryResponse> getStatusHistory(Long ticketId) {
        // Verify ticket exists before returning history
        TicketResponse ticket = ticketServiceClient.getTicketById(ticketId);

        List<TicketStatusHistory> history = statusHistoryRepository.findByTicketIdOrderByUpdatedAtDesc(ticketId);

        // If no status history exists, create a default "OPEN" status response
        if (history.isEmpty()) {
            log.info("No status history found for ticket {}, returning default OPEN status", ticketId);
            StatusHistoryResponse defaultStatus = new StatusHistoryResponse();
            defaultStatus.setId(0L); // Indicate this is a default/virtual record
            defaultStatus.setTicketId(ticketId);
            defaultStatus.setStatus(TicketStatus.OPEN);
            defaultStatus.setUpdatedBy("system");
            defaultStatus.setUpdatedAt(ticket.getCreatedAt()); // Use ticket creation time

            return List.of(defaultStatus);
        }

        return history.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StatusSummaryResponse getStatusSummary(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<TicketStatusHistory> statusUpdates = statusHistoryRepository.findByUpdatedAtBetween(startOfDay, endOfDay);

        // Count status updates by status type
        Map<String, Long> statusCounts = new HashMap<>();
        for (TicketStatus status : TicketStatus.values()) {
            statusCounts.put(status.name(), 0L);
        }

        for (TicketStatusHistory history : statusUpdates) {
            String statusName = history.getStatus().name();
            statusCounts.put(statusName, statusCounts.get(statusName) + 1);
        }

        long totalTickets = statusUpdates.size();
        return new StatusSummaryResponse(date, statusCounts, totalTickets);
    }

    public List<CurrentStatusResponse> getAllTicketsCurrentStatus() {
        log.info("Getting current status for all tickets");

        try {
            // Get all tickets from ticket service
            List<TicketResponse> allTickets = ticketServiceClient.getAllTickets();
            log.info("Found {} tickets", allTickets.size());

            return allTickets.stream()
                    .map(ticket -> getCurrentStatusForTicketResponse(ticket))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting all tickets current status: {}", e.getMessage());
            throw new RuntimeException("Unable to retrieve tickets status");
        }
    }

    private CurrentStatusResponse getCurrentStatusForTicketResponse(TicketResponse ticket) {
        // Get the most recent status for this ticket
        List<TicketStatusHistory> history = statusHistoryRepository
                .findByTicketIdOrderByUpdatedAtDesc(ticket.getTicketId());

        CurrentStatusResponse response = new CurrentStatusResponse();
        response.setTicketId(ticket.getTicketId());

        if (history.isEmpty()) {
            // No status history exists, return default "OPEN" status
            response.setCurrentStatus(TicketStatus.OPEN);
            response.setLastUpdatedBy("system");
            response.setLastUpdatedAt(ticket.getCreatedAt()); // Use ticket creation time
            response.setDefault(true); // Indicate this is a default status
        } else {
            // Return the most recent (current) status
            TicketStatusHistory currentStatus = history.get(0); // First item is most recent due to DESC order
            response.setCurrentStatus(currentStatus.getStatus());
            response.setLastUpdatedBy(currentStatus.getUpdatedBy());
            response.setLastUpdatedAt(currentStatus.getUpdatedAt());
            response.setDefault(false); // This is an actual status record
        }

        return response;
    }

    private StatusHistoryResponse mapToResponse(TicketStatusHistory history) {
        StatusHistoryResponse response = new StatusHistoryResponse();
        response.setId(history.getId());
        response.setTicketId(history.getTicketId());
        response.setStatus(history.getStatus());
        response.setUpdatedBy(history.getUpdatedBy());
        response.setUpdatedAt(history.getUpdatedAt());
        return response;
    }
}