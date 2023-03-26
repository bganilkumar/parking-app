package com.demo.parking.exception;

import org.springframework.core.NestedRuntimeException;

public class ParkingFullException extends NestedRuntimeException {
    public ParkingFullException(String msg) {
        super(msg);
    }
}
