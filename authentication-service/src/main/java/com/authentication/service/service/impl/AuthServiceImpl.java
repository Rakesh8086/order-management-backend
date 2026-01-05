package com.authentication.service.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authentication.service.entity.ERole;
import com.authentication.service.entity.Role;
import com.authentication.service.entity.User;
import com.authentication.service.exception.IncorrectDetailException;
import com.authentication.service.exception.SignupFailedException;
import com.authentication.service.repository.RoleRepository;
import com.authentication.service.repository.UserRepository;
import com.authentication.service.request.LoginRequest;
import com.authentication.service.request.PasswordChangeRequest;
import com.authentication.service.request.SignupRequest;
import com.authentication.service.response.MessageResponse;
import com.authentication.service.response.UserInfoResponse;
import com.authentication.service.security.jwt.JwtUtils;
import com.authentication.service.service.AuthService;
import com.authentication.service.service.UserDetailsImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final RoleRepository roleRepository;
	@Autowired
	private final PasswordEncoder encoder;
	@Autowired
	private final AuthenticationManager authenticationManager;
	@Autowired
	private final JwtUtils jwtUtils;
	
	@Override
	public Long registerUser(SignupRequest request) {
		if(userRepository.existsByMobileNumber(request.getMobileNumber())) {
		      throw new SignupFailedException(
		    		  "Mobile number already exists");
		}
	    if(userRepository.existsByEmail(request.getEmail())) {
	    	throw new SignupFailedException(
		    		  "Email already exists");
	    }

	    User user = new User(request.getUsername(),
	    		request.getEmail(), request.getMobileNumber(),
	            encoder.encode(request.getPassword()));

	    Set<String> allRoles = request.getRole();
	    Set<Role> roles = new HashSet<>(); 

	    if(allRoles == null) {
	      Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
	          .orElseThrow(() -> new RuntimeException("Role is not found."));
	      roles.add(userRole);
	    } 
	    else {
	    	for(String role:allRoles) {
	    	    if("ROLE_WAREHOUSE_MANAGER".equals(role)) {
	    	        Role warehouseRole = roleRepository.findByName(ERole.ROLE_WAREHOUSE_MANAGER)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(warehouseRole);

	    	    } 
	    	    else if("ROLE_FINANCE_OFFICER".equals(role)) {
	    	        Role financeRole = roleRepository.findByName(ERole.ROLE_FINANCE_OFFICER)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(financeRole);

	    	    } 
	    	    else {
	    	        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
	    	            .orElseThrow(() -> new RuntimeException("Role is not found."));
	    	        roles.add(userRole);
	    	    }
	    	}
	    }
	    user.setRoles(roles);
	    userRepository.save(user);
	    
	    return user.getId();
	}
	
	@Override
	public UserInfoResponse authenticateUser(LoginRequest loginRequest,
			HttpServletResponse response) {
	    Authentication authentication = authenticationManager
	        .authenticate(new UsernamePasswordAuthenticationToken(
	        		loginRequest.getMobileNumber(), loginRequest.getPassword()));
	    // store the authenticated user in security context of spring
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
	    List<String> roles = new ArrayList<>();
	    for(GrantedAuthority authority : userDetails.getAuthorities()) {
		   roles.add(authority.getAuthority());
		} 
	    response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
	    
	    return new UserInfoResponse(
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getMobileNumber(),
            userDetails.getEmail(),
            roles
	    );
	}
	
	@Override
	public MessageResponse logoutUser(HttpServletResponse response) {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		
		return new MessageResponse("You've been signed out");
	}
	
	@Override
	public String changePassword(PasswordChangeRequest 
			passwordChangeRequest) {
		Optional<User> userOptional = userRepository.findByMobileNumber(
				passwordChangeRequest.getMobileNumber());
		if(!userOptional.isPresent()) {
		    throw new IncorrectDetailException(
		    		"Incorrect mobile number is given");
		}
		User user = userOptional.get();
		if(!encoder.matches(
				passwordChangeRequest.getExistingPassword(),
	            user.getPassword())) {
	        throw new IncorrectDetailException(
	        		"Existing password is incorrectly given");
	    }
		if(!passwordChangeRequest.getMobileNumber().equals(user.getMobileNumber())){
			throw new IncorrectDetailException(
	        		"Mobile Number is incorrectly given");
		}
		user.setPassword(encoder.encode(
				passwordChangeRequest.getNewPassword()));
		userRepository.save(user);

	    return "Password changed successfully";
	}
}
