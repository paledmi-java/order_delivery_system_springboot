package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class ClientAddressIsInvalid extends RuntimeException {
    public ClientAddressIsInvalid(String message) {
        super(message);
    }
}
