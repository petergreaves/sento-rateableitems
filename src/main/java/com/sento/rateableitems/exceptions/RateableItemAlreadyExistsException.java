package com.sento.rateableitems.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RateableItemAlreadyExistsException extends RuntimeException {

	public RateableItemAlreadyExistsException(String exception) {
	    super(exception);
	  }
}
