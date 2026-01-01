package com.authentication.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.service.request.SignupRequest;
import com.authentication.service.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600) // * used for public end points
public class AuthController {
	@Autowired
	private final AuthService authService;
	
	@PostMapping("/signup")
	public ResponseEntity<Long> registerUser(
			@Valid @RequestBody SignupRequest signUpRequest) { 
	    return new ResponseEntity<>(
	    		authService.registerUser(signUpRequest), HttpStatus.CREATED);
	}
}