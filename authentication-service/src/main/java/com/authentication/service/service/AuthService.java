package com.authentication.service.service;

import org.springframework.stereotype.Service;

import com.authentication.service.request.LoginRequest;
import com.authentication.service.request.PasswordChangeRequest;
import com.authentication.service.request.SignupRequest;
import com.authentication.service.response.MessageResponse;
import com.authentication.service.response.UserInfoResponse;

import jakarta.servlet.http.HttpServletResponse;

@Service
public interface AuthService {
	Long registerUser(SignupRequest request);
	UserInfoResponse authenticateUser(LoginRequest request, HttpServletResponse response);
	MessageResponse logoutUser(HttpServletResponse response);
	String changePassword(PasswordChangeRequest request);
}
