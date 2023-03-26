package com.demo.parking.service;

import com.demo.parking.domain.VehicleType;
import com.demo.parking.payment.service.PaymentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ParkingServiceTest {

    @Test
    void parkAndExitTest() {
        ParkingSpace parkingSpace = new ParkingSpace(5);
        Parking parking = new Parking(parkingSpace);
        PaymentService paymentService = new PaymentService();
        ReflectionTestUtils.setField(paymentService, "price", 2.0D);
        ParkingService parkingService = new ParkingService(paymentService, parking);
        var ticket = parkingService.park("CAR-1", VehicleType.CAR);
        Assertions.assertThat(ticket).isNotNull();
        var payment = parkingService.payAndExit(ticket);
        Assertions.assertThat(payment).isNotNull();
    }

}