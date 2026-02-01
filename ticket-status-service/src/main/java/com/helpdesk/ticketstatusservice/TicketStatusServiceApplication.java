package com.helpdesk.ticketstatusservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.annotation.Validated;

@SpringBootApplication
@Validated
public class TicketStatusServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketStatusServiceApplication.class, args);
    }
}