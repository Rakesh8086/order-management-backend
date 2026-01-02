package com.authentication.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication.service.request.PasswordChangeRequest;
import com.authentication.service.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/access")
@CrossOrigin(origins = "*", maxAge = 3600) 
public class AccessController {
	@Autowired
	private final AuthService authService;
	
	@PutMapping("/change/password")
	@PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_WAREHOUSE_MANAGER') or hasRole('ROLE_FINANCE_OFFICER')")
	public ResponseEntity<String> changePassword(@Valid @RequestBody 
		PasswordChangeRequest request) {
		String message = authService.changePassword(request);
		return ResponseEntity.ok(message); 
	}
}
