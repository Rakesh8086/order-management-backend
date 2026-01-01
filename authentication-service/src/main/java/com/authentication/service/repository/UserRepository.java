package com.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.authentication.service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> fingByEmail(String email);
	Optional<User> findByMobileNumber(String mobileNumber);
	Boolean existsByMobileNumber(String mobileNumber);
	Boolean existsByEmail(String email);
}
