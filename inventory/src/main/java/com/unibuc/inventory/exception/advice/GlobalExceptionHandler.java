package com.unibuc.inventory.exception.advice;

import com.unibuc.inventory.exception.ErrorResponse;
import com.unibuc.inventory.exception.InsufficientStockException;
import com.unibuc.inventory.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ResourceNotFoundException ex) {
        // create an ErrorResponse
        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        // return the ResponseEntity
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(InsufficientStockException ex) {
        // create an ErrorResponse
        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        // return the ResponseEntity
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // exception handler for any type of exception thrown
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        // create an ErrorResponse
        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        // return the ResponseEntity
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}