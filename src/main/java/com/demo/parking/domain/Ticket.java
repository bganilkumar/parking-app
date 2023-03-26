package com.demo.parking.domain;

import java.time.LocalDateTime;

public record Ticket(Long id, LocalDateTime issuedOn) {
}
