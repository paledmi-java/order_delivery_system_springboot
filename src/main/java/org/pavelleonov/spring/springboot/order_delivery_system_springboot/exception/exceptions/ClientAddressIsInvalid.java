package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions;

import org.springframework.http.HttpStatus;

public class ClientAddressIsInvalid extends BusinessException {
    public ClientAddressIsInvalid(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ClientAddressIsInvalid(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
