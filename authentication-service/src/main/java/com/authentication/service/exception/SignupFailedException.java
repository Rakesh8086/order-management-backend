package com.authentication.service.exception;

public class SignupFailedException extends RuntimeException {
	public SignupFailedException(String message) {
	    super(message);
	}
}