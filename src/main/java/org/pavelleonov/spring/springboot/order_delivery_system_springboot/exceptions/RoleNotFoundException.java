package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
