package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.handlers;

import lombok.extern.slf4j.Slf4j;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.BusinessException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors
            (MethodArgumentNotValidException ex) {

        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error-> errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        "Ошибка валидации",
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now(),
                        errors
                )
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMissMatch
            (MethodArgumentTypeMismatchException ex){
        log.error("Argument miss match occurred", ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "Wrong input format",
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now(),
                        null
                ));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation
            (DataIntegrityViolationException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "Нарушение целостности данных (возможно дубликат)",
                        HttpStatus.CONFLICT.value(),
                        LocalDateTime.now(),
                        null
                ));
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex){

        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(
                ex.getMessage(),
                ex.getHttpStatus().value(),
                LocalDateTime.now(),
                null
        ));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex){

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        "Неверный формат JSON",
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex){

        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "Внутренняя ошибка сервера",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        LocalDateTime.now(),
                        null
                ));
    }


}
