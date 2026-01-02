package com.authentication.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
	private Long id;
	private String username;
	private String mobileNumber;
	private String email;
	private List<String> roles;
}
