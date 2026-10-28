package com.aprado.transacaoApi.handler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> transacaoNotValid(MethodArgumentNotValidException e) {
        // aqui faz sentido informarmos o objeto que ele tentou desserializar?
        // Isto é, faz sentido printar o objeto?
        StringBuilder sb = new StringBuilder("It was not possible to complete the transaction -> ");
        for (FieldError error: e.getFieldErrors()) {
            sb.append(error.getField());
            sb.append(": ");
            sb.append(error.getDefaultMessage());
            sb.append(" ");
        }
        sb.append(".");
        log.warn(sb.toString());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> transacaoNotReadable(HttpMessageNotReadableException e) {
        // Aqui faz sentido informar alguma coisa referente a requisição do cliente?
        log.warn("It was not possible to comprehend the request.");
        log.debug(e.getMessage());
        log.debug(e.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> unexpectedExceptionError(Exception e) {
        // temos um erro inesperado aqui...
        log.error("Unexpected error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
