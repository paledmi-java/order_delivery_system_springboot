package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
