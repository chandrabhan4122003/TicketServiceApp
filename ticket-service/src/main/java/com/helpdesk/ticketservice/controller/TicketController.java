package com.helpdesk.ticketservice.controller;

import com.helpdesk.ticketservice.dto.TicketCreateRequest;
import com.helpdesk.ticketservice.dto.TicketResponse;
import com.helpdesk.ticketservice.model.Priority;
import com.helpdesk.ticketservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @PostMapping("/create")
    public TicketResponse createTicket(@RequestBody TicketCreateRequest request) {
        return ticketService.createTicket(request);
    }
    
    @GetMapping("/{ticketId}")
    public TicketResponse getTicketById(@PathVariable Long ticketId) {
        return ticketService.getTicketById(ticketId);
    }
    
    @GetMapping("/employee/{employeeId}")
    public List<TicketResponse> getTicketsByEmployeeId(@PathVariable Long employeeId) {
        return ticketService.getTicketsByEmployeeId(employeeId);
    }
    
    @GetMapping("/priority/{priority}")
    public List<TicketResponse> getTicketsByPriority(@PathVariable Priority priority) {
        return ticketService.getTicketsByPriority(priority);
    }
}