package com.helpdesk.ticketservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatusServiceClient {

  private final WebClient webClient;

  public void createInitialStatus(Long ticketId) {
    try {
      log.info("Creating initial OPEN status for ticket ID: {}", ticketId);

      String requestBody = String.format("""
          {
              "ticketId": %d,
              "status": "OPEN",
              "updatedBy": "system"
          }
          """, ticketId);

      webClient.post()
          .uri("/status/update")
          .header("Content-Type", "application/json")
          .bodyValue(requestBody)
          .retrieve()
          .bodyToMono(String.class)
          .onErrorResume(ex -> {
            log.warn("Failed to create initial status for ticket {}: {}", ticketId, ex.getMessage());
            return Mono.empty(); // Don't fail ticket creation if status creation fails
          })
          .subscribe(); // Fire and forget - don't wait for response

    } catch (Exception e) {
      log.warn("Error creating initial status for ticket {}: {}", ticketId, e.getMessage());
      // Don't throw exception - ticket creation should succeed even if status
      // creation fails
    }
  }
}