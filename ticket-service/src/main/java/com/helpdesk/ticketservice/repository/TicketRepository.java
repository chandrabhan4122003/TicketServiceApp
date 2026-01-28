package com.helpdesk.ticketservice.repository;

import com.helpdesk.ticketservice.model.Priority;
import com.helpdesk.ticketservice.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByEmployeeId(Long employeeId);
    
    List<Ticket> findByPriority(Priority priority);
}