package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions;

public class BucketItemNotFoundException extends RuntimeException {
    public BucketItemNotFoundException(String message) {
        super(message);
    }
}
