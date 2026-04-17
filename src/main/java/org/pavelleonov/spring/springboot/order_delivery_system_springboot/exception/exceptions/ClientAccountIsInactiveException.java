package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions;

import org.springframework.http.HttpStatus;

public class ClientAccountIsInactiveException extends BusinessException {
    public ClientAccountIsInactiveException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ClientAccountIsInactiveException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
