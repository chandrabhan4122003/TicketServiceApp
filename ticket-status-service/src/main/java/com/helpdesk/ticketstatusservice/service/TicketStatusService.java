package com.helpdesk.ticketstatusservice.service;

import com.helpdesk.ticketstatusservice.client.TicketServiceClient;
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
    
    public List<StatusHistoryResponse> getStatusHistory(Long ticketId) {
        // Verify ticket exists before returning history
        ticketServiceClient.getTicketById(ticketId);
        
        List<TicketStatusHistory> history = statusHistoryRepository.findByTicketIdOrderByUpdatedAtDesc(ticketId);
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