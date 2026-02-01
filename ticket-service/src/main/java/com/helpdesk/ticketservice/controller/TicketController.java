package com.helpdesk.ticketservice.controller;

import com.helpdesk.ticketservice.dto.TicketCreateRequest;
import com.helpdesk.ticketservice.dto.TicketResponse;
import com.helpdesk.ticketservice.model.Priority;
import com.helpdesk.ticketservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Validated
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/create")
    public TicketResponse createTicket(@Valid @RequestBody TicketCreateRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping("/{ticketId}")
    public TicketResponse getTicketById(
            @PathVariable @Positive(message = "Ticket ID must be a positive number") Long ticketId) {
        return ticketService.getTicketById(ticketId);
    }

    @GetMapping("/employee/{employeeId}")
    public List<TicketResponse> getTicketsByEmployeeId(
            @PathVariable @Positive(message = "Employee ID must be a positive number") Long employeeId) {
        return ticketService.getTicketsByEmployeeId(employeeId);
    }

    @GetMapping("/priority/{priority}")
    public List<TicketResponse> getTicketsByPriority(@PathVariable Priority priority) {
        return ticketService.getTicketsByPriority(priority);
    }

    @GetMapping("/all")
    public List<TicketResponse> getAllTickets() {
        return ticketService.getAllTickets();
    }
}