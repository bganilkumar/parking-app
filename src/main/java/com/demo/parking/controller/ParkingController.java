package com.demo.parking.controller;

import com.demo.parking.domain.Ticket;
import com.demo.parking.domain.Vehicle;
import com.demo.parking.domain.VehicleType;
import com.demo.parking.exception.InvalidTicketException;
import com.demo.parking.exception.ParkingApplicationException;
import com.demo.parking.exception.ParkingFullException;
import com.demo.parking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
@Slf4j
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping(value = "/park", consumes = "text/plain")
    @ResponseBody
    public ResponseEntity<Object> park(@RequestBody String registrationNumber) {
        try {
            log.debug("Attempting to park vehicle {} of type {}", registrationNumber, VehicleType.CAR);
            return new ResponseEntity<>(parkingService.park(registrationNumber, VehicleType.CAR), HttpStatus.ACCEPTED);
        } catch (ParkingFullException parkingFullException) {
            log.error("Exception occurred while trying to park vehicle {}", registrationNumber, parkingFullException);
            return new ResponseEntity<>(parkingFullException.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/exit")
    @ResponseBody
    public ResponseEntity<Object> exit(@RequestBody Ticket ticket) {
        try {
            log.debug("Attempting to exit vehicle for ticked {}", ticket);
            return new ResponseEntity<>(parkingService.payAndExit(ticket), HttpStatus.OK);
        } catch (InvalidTicketException invalidTicketException) {
            log.error("Invalid ticket {} provided.", ticket, invalidTicketException);
            return new ResponseEntity<>(invalidTicketException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ParkingApplicationException parkingApplicationException) {
            log.error("Exception while tyring to exit for ticket {}.", ticket, parkingApplicationException);
            return new ResponseEntity<>(parkingApplicationException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/vehicles")
    @ResponseBody
    public Collection<Vehicle> vehicles() {
        log.debug("Fetching the vehicles list.");
        return parkingService.vehicleList();
    }


}
