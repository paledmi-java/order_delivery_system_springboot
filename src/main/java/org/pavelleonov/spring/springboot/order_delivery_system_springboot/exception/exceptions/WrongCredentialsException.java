package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions;

import org.springframework.http.HttpStatus;

public class WrongCredentialsException extends BusinessException {
    public WrongCredentialsException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public WrongCredentialsException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
