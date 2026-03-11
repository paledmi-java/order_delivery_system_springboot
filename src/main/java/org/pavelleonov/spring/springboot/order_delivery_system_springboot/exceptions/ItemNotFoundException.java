package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
