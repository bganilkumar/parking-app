package com.demo.parking.payment.service;

import com.demo.parking.domain.Payment;
import com.demo.parking.domain.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class PaymentService {

    @Value("${parking.price:2.0}")
    private double price;

    public Payment calculatePayment(Ticket ticket) {
        var exitTime = getExitTime();
        var parkedHrs = parkedHours(ticket, exitTime);
        var calculatedPrice = parkedHrs * price;
        log.info("Price calculated for the ticket {} is {} and total parking time is {}", ticket, price, parkedHrs);
        return new Payment(ticket, calculatedPrice, exitTime);
    }

    private long parkedHours(Ticket ticket, LocalDateTime exitTime) {
        var totalTimeInParkingInMins = ChronoUnit.MINUTES.between(ticket.issuedOn(), exitTime);
        var totalTimeInParkingInHours = ChronoUnit.HOURS.between(ticket.issuedOn(), exitTime);
        if (totalTimeInParkingInMins == 0 || totalTimeInParkingInHours == 0) {
            return 1;
        }
        var totalTimeToHours = totalTimeInParkingInMins / 60D;
        return totalTimeToHours - totalTimeInParkingInHours == 0 ? totalTimeInParkingInHours : totalTimeInParkingInHours + 1;
    }

    private LocalDateTime getExitTime() {
        return LocalDateTime.now();
    }

}
