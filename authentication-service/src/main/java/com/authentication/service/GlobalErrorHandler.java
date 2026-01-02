package com.authentication.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.authentication.service.exception.IncorrectDetailException;
import com.authentication.service.exception.SignupFailedException;

@ControllerAdvice
public class GlobalErrorHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName;
            if(error instanceof FieldError fieldError) {
                fieldName = fieldError.getField();
            } 
            else {
                fieldName = error.getObjectName(); 
            }
            String message = error.getDefaultMessage();
            errorMap.put(fieldName, message);
        });
        
        // 400 Bad request
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST); 
    }
	
	@ExceptionHandler(SignupFailedException.class)
    public ResponseEntity<String> handleResourceNotFoundException(SignupFailedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(IncorrectDetailException.class)
    public ResponseEntity<String> handleIncorrectDetailException(
    		IncorrectDetailException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

