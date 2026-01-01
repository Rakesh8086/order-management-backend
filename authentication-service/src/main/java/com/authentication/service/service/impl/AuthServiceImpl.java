package com.authentication.service.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authentication.service.entity.ERole;
import com.authentication.service.entity.Role;
import com.authentication.service.entity.User;
import com.authentication.service.exception.SignupFailedException;
import com.authentication.service.repository.RoleRepository;
import com.authentication.service.repository.UserRepository;
import com.authentication.service.request.SignupRequest;
import com.authentication.service.service.AuthService;

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
}
