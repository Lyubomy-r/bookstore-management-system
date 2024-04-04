package com.manage.bookstore.bookstore.exception.controllerAdvice;

import com.manage.bookstore.bookstore.exception.GeneralException;
import com.manage.bookstore.bookstore.exception.model.ErrorResponse;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;


@ControllerAdvice
public class GeneralRestExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GeneralRestExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlerGeneralException(GeneralException exc) {
        ErrorResponse userErrorResponse = new ErrorResponse(
                exc.getHttpStatus().value(),
                exc.getMessage(),
                new Timestamp(System.currentTimeMillis()));
        logger.warn("From GeneralRestExceptionHandler method -handlerGeneralException- send message error ({})",
                userErrorResponse.getMessage());

        return new ResponseEntity<>(userErrorResponse, exc.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlerRunTimeException (StatusRuntimeException exc) {
        ErrorResponse userErrorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exc.getMessage(),
                new Timestamp(System.currentTimeMillis()));
        logger.warn("From GeneralRestExceptionHandler method -handlerRunTimeException- send message error ({})",
                userErrorResponse.getMessage());

        return new ResponseEntity<>(userErrorResponse, HttpStatus.valueOf(userErrorResponse.getStatus()));
    }

}
