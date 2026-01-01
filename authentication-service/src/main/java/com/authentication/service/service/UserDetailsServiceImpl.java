package com.authentication.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authentication.service.entity.User;
import com.authentication.service.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
    User user = userRepository.findByMobileNumber(mobileNumber)
        .orElseThrow(() -> new UsernameNotFoundException(
        		"User Not Found with mobile number: " + mobileNumber));

    return UserDetailsImpl.build(user);
  }

}
