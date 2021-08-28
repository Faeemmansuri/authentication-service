package com.faeem.authenticationservice.config;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.faeem.authenticationservice.exception.BadRequestException;
import com.faeem.authenticationservice.response.ErrorResponse;

@ControllerAdvice
public class AuthenticationControllerAdvice extends ResponseEntityExceptionHandler {
	
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> unhandledException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(new Date(), ex.getMessage(), null,request.getDescription(false));
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(new Date(), ex.getMessage(), null, request.getDescription(false));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> "'" + ((FieldError) error).getField() + "' " + error.getDefaultMessage()).collect(Collectors.toList());
        
        ErrorResponse errorResponse = new ErrorResponse(new Date(), null, errors, request.getDescription(false));
        return ResponseEntity.status(status).body(errorResponse);
    }

}
