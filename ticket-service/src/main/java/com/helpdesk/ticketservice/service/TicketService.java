package com.helpdesk.ticketservice.service;

import com.helpdesk.ticketservice.client.StatusServiceClient;
import com.helpdesk.ticketservice.dto.TicketCreateRequest;
import com.helpdesk.ticketservice.dto.TicketResponse;
import com.helpdesk.ticketservice.exception.TicketNotFoundException;
import com.helpdesk.ticketservice.model.Priority;
import com.helpdesk.ticketservice.model.Ticket;
import com.helpdesk.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final StatusServiceClient statusServiceClient;

    public TicketResponse createTicket(TicketCreateRequest request) {
        log.info("Creating ticket for employee: {}", request.getEmployeeName());

        // 1. Create and save the ticket
        Ticket ticket = new Ticket();
        ticket.setEmployeeId(request.getEmployeeId());
        ticket.setEmployeeName(request.getEmployeeName());
        ticket.setIssueCategory(request.getIssueCategory());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Ticket created with ID: {}", savedTicket.getTicketId());

        // 2. Automatically create initial "OPEN" status
        statusServiceClient.createInitialStatus(savedTicket.getTicketId());

        return mapToResponse(savedTicket);
    }

    public TicketResponse getTicketById(Long ticketId) {
        log.info("Getting ticket by ID: {}", ticketId);
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent()) {
            log.info("Ticket found: {}", ticket.get().getTicketId());
            return mapToResponse(ticket.get());
        }
        log.warn("Ticket not found, throwing exception for ID: {}", ticketId);
        throw new TicketNotFoundException("Ticket with ID " + ticketId + " not found");
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

    public List<TicketResponse> getAllTickets() {
        log.info("Getting all tickets");
        List<Ticket> tickets = ticketRepository.findAll();
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