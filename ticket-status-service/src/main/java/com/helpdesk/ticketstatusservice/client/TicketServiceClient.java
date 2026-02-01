package com.helpdesk.ticketstatusservice.client;

import com.helpdesk.ticketstatusservice.dto.TicketResponse;
import com.helpdesk.ticketstatusservice.exception.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

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
                            ex -> {
                                log.warn("Ticket {} not found in ticket service", ticketId);
                                return Mono.error(
                                        new TicketNotFoundException("Ticket with ID " + ticketId + " not found"));
                            })
                    .onErrorResume(WebClientResponseException.class,
                            ex -> {
                                if (ex.getStatusCode().is4xxClientError()) {
                                    log.warn("Client error from ticket service: {} - {}", ex.getStatusCode(),
                                            ex.getMessage());
                                    if (ex.getStatusCode().value() == 404) {
                                        return Mono.error(new TicketNotFoundException(
                                                "Ticket with ID " + ticketId + " not found"));
                                    } else if (ex.getStatusCode().value() == 400) {
                                        return Mono
                                                .error(new IllegalArgumentException("Invalid ticket ID: " + ticketId));
                                    }
                                }
                                log.error("HTTP error calling ticket service: {} - {}", ex.getStatusCode(),
                                        ex.getMessage());
                                return Mono.error(
                                        new RuntimeException("Ticket service returned error: " + ex.getStatusCode()));
                            })
                    .onErrorResume(Exception.class,
                            ex -> {
                                // Don't wrap specific exceptions - let them bubble up
                                if (ex instanceof TicketNotFoundException ||
                                        ex instanceof IllegalArgumentException) {
                                    return Mono.error(ex);
                                }
                                log.error("Error calling ticket service: {}", ex.getMessage());
                                return Mono.error(
                                        new RuntimeException("Ticket service is unavailable: " + ex.getMessage()));
                            })
                    .block(); // Convert to synchronous call

        } catch (TicketNotFoundException e) {
            // Re-throw TicketNotFoundException as-is
            log.debug("Re-throwing TicketNotFoundException: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            // Re-throw IllegalArgumentException as-is
            log.debug("Re-throwing IllegalArgumentException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to get ticket {}: {}", ticketId, e.getMessage());
            throw e;
        }
    }

    public List<TicketResponse> getAllTickets() {
        try {
            log.info("Calling ticket service to get all tickets");

            return webClient.get()
                    .uri("/tickets/all")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TicketResponse>>() {
                    })
                    .onErrorResume(Exception.class,
                            ex -> {
                                log.error("Error calling ticket service for all tickets: {}", ex.getMessage());
                                return Mono.error(new RuntimeException("Ticket service is unavailable"));
                            })
                    .block(); // Convert to synchronous call

        } catch (Exception e) {
            log.error("Failed to get all tickets: {}", e.getMessage());
            throw e;
        }
    }
}