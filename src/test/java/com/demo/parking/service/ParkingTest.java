package com.demo.parking.service;

import com.demo.parking.domain.Ticket;
import com.demo.parking.domain.VehicleType;
import com.demo.parking.exception.InvalidTicketException;
import com.demo.parking.exception.ParkingFullException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.LongStream;

class ParkingTest {

    @Test
    void parkTest() {
        ParkingSpace parkingSpace = new ParkingSpace(5);
        Parking parking = new Parking(parkingSpace);
        var expectedIssuedOn = LocalDateTime.now();
        var ticket = parking.park("Test-Car", VehicleType.CAR);
        Assertions.assertThat(ticket).isNotNull().satisfies(ticketCreated -> {
            Assertions.assertThat(ticketCreated.id()).isEqualTo(1);
            Assertions.assertThat(ticketCreated.issuedOn()).isAfterOrEqualTo(expectedIssuedOn);
        });
    }

    @Test
    void parkingFullTest() {
        ParkingSpace parkingSpace = new ParkingSpace(5);
        Parking parking = new Parking(parkingSpace);
        var expectedIssuedOn = LocalDateTime.now();
        LongStream.range(1, 6).boxed().forEach(range -> {
            var ticket = parking.park("Test-Car-" + range, VehicleType.CAR);
            Assertions.assertThat(ticket).isNotNull().satisfies(ticketCreated -> {
                Assertions.assertThat(ticketCreated.id()).isEqualTo(range);
                Assertions.assertThat(ticketCreated.issuedOn()).isAfterOrEqualTo(expectedIssuedOn);
            });
        });
        Assertions.assertThatExceptionOfType(ParkingFullException.class)
                .isThrownBy(() -> parking.park("Test-Car-200", VehicleType.CAR))
                .withMessage("Parking is currently full.");
    }

    @Test
    void unParkTest() {
        ParkingSpace parkingSpace = new ParkingSpace(5);
        Parking parking = new Parking(parkingSpace);
        var tickets = new ArrayList<Ticket>();
        LongStream.range(1, 6).boxed().forEach(range -> {
            var ticket = parking.park("Test-Car-" + range, VehicleType.CAR);
            tickets.add(ticket);
        });
        Assertions.assertThatExceptionOfType(ParkingFullException.class)
                .isThrownBy(() -> parking.park("Test-Car-200", VehicleType.CAR));
        tickets.forEach(ticket -> Assertions.assertThatNoException().isThrownBy(() -> parking.unpark(ticket)));
        Assertions.assertThatExceptionOfType(InvalidTicketException.class)
                .isThrownBy(() -> parking.unpark(new Ticket(700L, LocalDateTime.now())));
    }

}