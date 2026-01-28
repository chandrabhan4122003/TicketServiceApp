package com.helpdesk.ticketstatusservice.client;

import com.helpdesk.ticketstatusservice.dto.TicketResponse;
import com.helpdesk.ticketstatusservice.exception.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketServiceClient {
    
    private final WebClient webClient;
    
    public TicketResponse getTicketById(Long ticketId) {
        try {
            log.info("Calling ticket service to verify ticket ID: {}", ticketId);
            
            return webClient.get()
                    .uri("/tickets/{ticketId}", ticketId)
                    .retrieve()
                    .bodyToMono(TicketResponse.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, 
                            ex -> Mono.error(new TicketNotFoundException("Ticket with ID " + ticketId + " not found")))
                    .onErrorResume(Exception.class, 
                            ex -> {
                                log.error("Error calling ticket service: {}", ex.getMessage());
                                return Mono.error(new RuntimeException("Ticket service is unavailable"));
                            })
                    .block(); // Convert to synchronous call
                    
        } catch (Exception e) {
            log.error("Failed to get ticket {}: {}", ticketId, e.getMessage());
            throw e;
        }
    }
}