package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
