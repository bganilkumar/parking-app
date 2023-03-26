package com.demo.parking.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Vehicle {

    private final String registrationNumber;

    private final Ticket ticket;

    private final VehicleType vehicleType;
}
