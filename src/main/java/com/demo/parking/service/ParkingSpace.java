package com.demo.parking.service;

import com.demo.parking.exception.ParkingApplicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ParkingSpace {

    private final AtomicInteger parkingSpaceCounter;

    private final int maxParkingSpaceAllowed;

    public ParkingSpace(@Value("${parking.space.max:100}") int maxParkingSpaceAllowed) {
        this.maxParkingSpaceAllowed = maxParkingSpaceAllowed;
        this.parkingSpaceCounter = new AtomicInteger(maxParkingSpaceAllowed);
    }

    public boolean isAvailableToPark() {
        return parkingSpaceCounter.get() > 0;
    }

    protected void park() {
        parkingSpaceCounter.decrementAndGet();
    }

    protected void unpark() {
        if (parkingSpaceCounter.get() == maxParkingSpaceAllowed) {
            throw new ParkingApplicationException("Trying to un-park when no vehicles left");
        }
        parkingSpaceCounter.incrementAndGet();
    }
}
