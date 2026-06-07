package com.demo.travelcardsystem.controller;

import com.demo.travelcardsystem.exception.InvalidCardException;
import com.demo.travelcardsystem.exception.InvalidDataProvidedException;
import com.demo.travelcardsystem.exception.InvalidRechargeAmount;
import com.demo.travelcardsystem.exception.TravelCardException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler({InvalidCardException.class, InvalidRechargeAmount.class})
    public ResponseEntity<String> handleInvalidRequestException(TravelCardException invalidCardException) {
        log.error("Invalid Request Exception occurred: {}", invalidCardException.getMessage(), invalidCardException);
        return new ResponseEntity<>(invalidCardException.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(InvalidDataProvidedException.class)
    public ResponseEntity<String> handleInvalidDataProvidedException(InvalidDataProvidedException invalidDataProvidedException) {
        log.error("Invalid Data Provided Exception occurred", invalidDataProvidedException);
        return new ResponseEntity<>("Invalid request! Please check input", HttpStatus.BAD_REQUEST);
    }

}
