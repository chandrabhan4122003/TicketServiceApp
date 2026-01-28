package com.helpdesk.ticketstatusservice.repository;

import com.helpdesk.ticketstatusservice.model.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, Long> {
    
    List<TicketStatusHistory> findByTicketIdOrderByUpdatedAtDesc(Long ticketId);
    
    List<TicketStatusHistory> findByUpdatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}