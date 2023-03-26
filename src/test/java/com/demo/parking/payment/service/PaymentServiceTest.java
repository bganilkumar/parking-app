package com.demo.parking.payment.service;

import com.demo.parking.domain.Ticket;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

class PaymentServiceTest {

    @Test
    void paymentTest() {
        PaymentService paymentService = new PaymentService();
        var price = 5D;
        ReflectionTestUtils.setField(paymentService, "price", price);
        Clock FIXED_CLOCK = Clock.fixed(Instant.ofEpochMilli(System.currentTimeMillis() - 3600002L), ZoneId.of("UTC"));
        var ticket = new Ticket(1L, LocalDateTime.now(FIXED_CLOCK));
        var expectedTime = LocalDateTime.now();
        var payment = paymentService.calculatePayment(ticket);
        Assertions.assertThat(payment).isNotNull().satisfies(payment1 -> {
            Assertions.assertThat(payment1.price()).isEqualTo(price * 2);
            Assertions.assertThat(payment1.exitTime()).isAfterOrEqualTo(expectedTime);
        });
    }

    @Test
    void paymentForMinHrsTest() {
        PaymentService paymentService = new PaymentService();
        var price = 5D;
        ReflectionTestUtils.setField(paymentService, "price", price);
        Clock FIXED_CLOCK = Clock.fixed(Instant.ofEpochMilli(System.currentTimeMillis() - 3600L), ZoneId.of("UTC"));
        var ticket = new Ticket(1L, LocalDateTime.now(FIXED_CLOCK));
        var expectedTime = LocalDateTime.now();
        var payment = paymentService.calculatePayment(ticket);
        Assertions.assertThat(payment).isNotNull().satisfies(payment1 -> {
            Assertions.assertThat(payment1.price()).isEqualTo(price * 1);
            Assertions.assertThat(payment1.exitTime()).isAfterOrEqualTo(expectedTime);
        });
    }

}