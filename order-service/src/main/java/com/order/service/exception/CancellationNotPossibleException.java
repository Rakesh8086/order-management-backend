package com.order.service.exception;

public class CancellationNotPossibleException extends RuntimeException{
	public CancellationNotPossibleException(String message) {
	    super(message);
	}
}