package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions;

import org.springframework.http.HttpStatus;

public class ClientAddressNotFoundException extends BusinessException {
    public ClientAddressNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ClientAddressNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}
