package com.sento.rateableitems.controllers;

import com.sento.rateableitems.exceptions.ErrorDetails;
import com.sento.rateableitems.exceptions.InvalidRateableItemException;
import com.sento.rateableitems.exceptions.RateableItemAlreadyExistsException;
import com.sento.rateableitems.exceptions.RateableItemNotFoundException;

import com.sento.rateableitems.model.RateableItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RateableItemServiceControllerAdvice {

    @ExceptionHandler(RateableItemNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleRateableItemNotFoundException(RateableItemNotFoundException ex) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), "Rateable Item not found with this ID");

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRateableItemException.class)
    public final ResponseEntity<ErrorDetails> handleInvalidRateableItemException(InvalidRateableItemException ex) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), "RateableItem attributes are invalid in update/create or doesn't match request path.");

        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);

    }


    @ExceptionHandler(RateableItemAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleOrganisationAlreadyExistsException(RateableItemAlreadyExistsException ex) {


        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), "A rateable item already exists with this ID");

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }
}
