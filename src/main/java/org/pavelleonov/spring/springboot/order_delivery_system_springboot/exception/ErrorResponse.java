package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse
        (String message,
         int httpStatus,
         LocalDateTime timeStamp,
         Map<String, String> errors) {
}
