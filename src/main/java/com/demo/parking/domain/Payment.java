package com.demo.parking.domain;

import java.time.LocalDateTime;

public record Payment(Ticket ticket, Double price, LocalDateTime exitTime) {
}
