package com.helpdesk.ticketservice.service;

import com.helpdesk.ticketservice.dto.TicketCreateRequest;
import com.helpdesk.ticketservice.dto.TicketResponse;
import com.helpdesk.ticketservice.model.Priority;
import com.helpdesk.ticketservice.model.Ticket;
import com.helpdesk.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final TicketRepository ticketRepository;
    
    public TicketResponse createTicket(TicketCreateRequest request) {
        Ticket ticket = new Ticket();
        ticket.setEmployeeId(request.getEmployeeId());
        ticket.setEmployeeName(request.getEmployeeName());
        ticket.setIssueCategory(request.getIssueCategory());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        
        Ticket savedTicket = ticketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }
    
    public TicketResponse getTicketById(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent()) {
            return mapToResponse(ticket.get());
        }
        return null; // Simple approach - return null if not found
    }
    
    public List<TicketResponse> getTicketsByEmployeeId(Long employeeId) {
        List<Ticket> tickets = ticketRepository.findByEmployeeId(employeeId);
        return tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TicketResponse> getTicketsByPriority(Priority priority) {
        List<Ticket> tickets = ticketRepository.findByPriority(priority);
        return tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setTicketId(ticket.getTicketId());
        response.setEmployeeId(ticket.getEmployeeId());
        response.setEmployeeName(ticket.getEmployeeName());
        response.setIssueCategory(ticket.getIssueCategory());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setCreatedAt(ticket.getCreatedAt());
        return response;
    }
}