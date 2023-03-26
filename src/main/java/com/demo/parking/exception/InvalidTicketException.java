package com.demo.parking.exception;

import org.springframework.core.NestedRuntimeException;

public class InvalidTicketException extends NestedRuntimeException {

    public InvalidTicketException(String msg) {
        super(msg);
    }
}
