package com.authentication.service.service;

import com.authentication.service.request.SignupRequest;

public interface AuthService {
	Long registerUser(SignupRequest request);
}
