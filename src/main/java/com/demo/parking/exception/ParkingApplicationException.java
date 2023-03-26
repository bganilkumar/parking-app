package com.demo.parking.exception;

import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ParkingApplicationException extends NestedRuntimeException {
    public ParkingApplicationException(String msg) {
        super(msg);
    }
}
