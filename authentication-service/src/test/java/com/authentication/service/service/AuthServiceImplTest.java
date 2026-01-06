package com.authentication.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.authentication.service.entity.ERole;
import com.authentication.service.entity.Role;
import com.authentication.service.entity.User;
import com.authentication.service.exception.SignupFailedException;
import com.authentication.service.repository.RoleRepository;
import com.authentication.service.repository.UserRepository;
import com.authentication.service.request.LoginRequest;
import com.authentication.service.request.PasswordChangeRequest;
import com.authentication.service.request.SignupRequest;
import com.authentication.service.response.MessageResponse;
import com.authentication.service.response.UserInfoResponse;
import com.authentication.service.security.jwt.JwtUtils;
import com.authentication.service.service.impl.AuthServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private HttpServletResponse response;

    @Test
    void registerUser_mobileAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setMobileNumber("9999999999");
        when(userRepository.existsByMobileNumber("9999999999")).thenReturn(true);
        assertThrows(SignupFailedException.class,
                () -> authService.registerUser(request));
    }

    @Test
    void registerUser_emailAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setMobileNumber("9999999999");
        request.setEmail("test@mail.com");
        when(userRepository.existsByMobileNumber(any()))
                .thenReturn(false);
        when(userRepository.existsByEmail("test@mail.com"))
                .thenReturn(true);
        assertThrows(SignupFailedException.class,
                () -> authService.registerUser(request));
    }

    @Test
    void registerUser_defaultRoleAssigned() {
        SignupRequest request = new SignupRequest();
        request.setUsername("user");
        request.setEmail("user@mail.com");
        request.setMobileNumber("9999999999");
        request.setPassword("pass");

        Role role = new Role();
        role.setName(ERole.ROLE_CUSTOMER);

        when(userRepository.existsByMobileNumber(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(encoder.encode(any())).thenReturn("encoded");
        when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        Long id = authService.registerUser(request);
        assertNotNull(id);
    }

    @Test
    void authenticateUser_success() {
        LoginRequest login = new LoginRequest();
        login.setMobileNumber("9999999999");
        login.setPassword("pass");

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtCookie(any()))
                .thenReturn(ResponseCookie.from("jwt", "token").build());
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("user");
        when(userDetails.getMobileNumber()).thenReturn("9999999999");
        when(userDetails.getEmail()).thenReturn("user@mail.com");
        UserInfoResponse responseObj =
                authService.authenticateUser(login, response);
        assertEquals("user", responseObj.getUsername());
    }
    
    @Test
    void logoutUser_success() {
        when(jwtUtils.getCleanJwtCookie())
                .thenReturn(ResponseCookie.from("jwt", "").build());
        MessageResponse responseObj = authService.logoutUser(response);
        assertEquals("You've been signed out", responseObj.getMessage());
    }

    @Test
    void changePassword_success() {
        String mobile = "9999999999";
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setMobileNumber(mobile);
        request.setExistingPassword("old");
        request.setNewPassword("new");

        User user = new User();
        user.setMobileNumber(mobile);
        user.setPassword("encodedOld");

        when(userRepository.findByMobileNumber(mobile)).thenReturn(Optional.of(user));
        when(encoder.matches("old", "encodedOld")).thenReturn(true);
        when(encoder.encode("new")).thenReturn("encodedNew");
        // returns User obj
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String result = authService.changePassword(request);
        assertEquals("Password changed successfully", result);
    }
}