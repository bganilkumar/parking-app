package com.demo.parking.service;

import com.demo.parking.domain.Payment;
import com.demo.parking.domain.Ticket;
import com.demo.parking.domain.Vehicle;
import com.demo.parking.domain.VehicleType;
import com.demo.parking.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingService {

    private final PaymentService paymentService;

    private final Parking parking;

    public Ticket park(String registrationNumber, VehicleType vehicleType) {
        log.info("Attempting to park vehicle with registration number {}", registrationNumber);
        return parking.park(registrationNumber, vehicleType);
    }

    public Payment payAndExit(Ticket ticket) {
        log.info("Attempting to exit vehicle with ticket {}", ticket);
        var payment = paymentService.calculatePayment(ticket);
        parking.unpark(ticket);
        return payment;
    }

    public Collection<Vehicle> vehicleList() {
        return parking.list();
    }


}
