package com.demo.parking.service;

import com.demo.parking.domain.Ticket;
import com.demo.parking.domain.Vehicle;
import com.demo.parking.domain.VehicleType;
import com.demo.parking.exception.InvalidTicketException;
import com.demo.parking.exception.ParkingFullException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Slf4j
public class Parking {

    private final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private final ParkingSpace parkingSpace;

    private final Map<Long, Vehicle> parking = new ConcurrentHashMap<>();

    public Ticket park(String registrationNumber, VehicleType vehicleType) {
        if (parkingSpace.isAvailableToPark()) {
            parkingSpace.park();
            var id = ID_GENERATOR.incrementAndGet();
            var ticket = new Ticket(id, LocalDateTime.now());
            var vehicle = new Vehicle(registrationNumber, ticket, vehicleType);
            parking.put(id, vehicle);
            log.info("Parking available and successfully parked vehicle {} and generated ticket {}", vehicle, ticket);
            return ticket;
        }
        log.warn("No Parking available for the vehicle with registration number {}", registrationNumber);
        throw new ParkingFullException("Parking is currently full.");
    }

    public void unpark(Ticket ticket) {
        var vehicle = parking.remove(ticket.id());
        if (vehicle == null) {
            log.warn("Invalid Ticket {} provided.", ticket);
            throw new InvalidTicketException("Ticket is not valid.");
        }
        parkingSpace.unpark();
        log.info("Successfully un-parked the vehicle {} for ticket {}", vehicle, ticket);
    }

    public Collection<Vehicle> list() {
        return parking.values();
    }

}
