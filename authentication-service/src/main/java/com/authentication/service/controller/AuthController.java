package com.authentication.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.service.request.LoginRequest;
import com.authentication.service.request.PasswordChangeRequest;
import com.authentication.service.request.SignupRequest;
import com.authentication.service.response.MessageResponse;
import com.authentication.service.response.UserInfoResponse;
import com.authentication.service.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
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
	
	@PostMapping("/signin")
	public ResponseEntity<UserInfoResponse> authenticateUser(
			@Valid @RequestBody LoginRequest request,
			HttpServletResponse response) { 
	    return new ResponseEntity<>(
	    		authService.authenticateUser(request, response), HttpStatus.OK);
	}
	
	@PostMapping("/signout")
	public ResponseEntity<MessageResponse> logoutUser(HttpServletResponse response) {
	    
		return new ResponseEntity<>(
	    		authService.logoutUser(response), HttpStatus.OK);
	} 
	
	@PutMapping("/change/password")
	@PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_WAREHOUSE_MANAGER') or hasRole('ROLE_FINANCE_OFFICER')")
	public ResponseEntity<String> changePassword(@Valid @RequestBody 
		PasswordChangeRequest request) {
		String message = authService.changePassword(request);
		return ResponseEntity.ok(message); 
	}
}