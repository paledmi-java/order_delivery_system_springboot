package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
