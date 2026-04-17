package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions;

import org.springframework.http.HttpStatus;

public class ClientNotFoundException extends BusinessException {

    public ClientNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}
