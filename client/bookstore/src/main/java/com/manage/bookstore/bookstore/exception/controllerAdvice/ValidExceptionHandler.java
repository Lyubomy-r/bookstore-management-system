package com.manage.bookstore.bookstore.exception.controllerAdvice;

import com.manage.bookstore.bookstore.exception.model.ValidResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.sql.Timestamp;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ValidExceptionHandler.class);

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ValidResponse> handleWebExchangeException(WebExchangeBindException e) {
        var errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ValidResponse validResponse=new ValidResponse();
        validResponse.setErrorsList(errors);
        validResponse.setStatus(e.getStatus().value());
        validResponse.setTimeStamp(new Timestamp(System.currentTimeMillis()));
        logger.warn("From ValidExceptionHandler method -handleWebExchangeException- send message error ({})",
                validResponse.getErrorsList());
        return ResponseEntity.badRequest().body(validResponse);
    }
}
