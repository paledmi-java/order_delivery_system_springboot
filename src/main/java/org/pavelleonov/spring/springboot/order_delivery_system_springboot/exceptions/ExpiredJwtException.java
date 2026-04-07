package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class ExpiredJwtException extends RuntimeException {
    public ExpiredJwtException(String message) {
        super(message);
    }
}
