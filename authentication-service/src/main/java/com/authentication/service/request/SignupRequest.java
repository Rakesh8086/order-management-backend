package com.authentication.service.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
	@NotBlank
	@Size(min = 1, max = 20)
	private String username;
	@NotBlank
    @Size(max = 50)
    @Email
    private String email;
	@NotBlank
	@Pattern(regexp = "\\d{10}", message = "Mobile number must be a 10-digit number")
	private String mobileNumber;
	private Set<String> role;
    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Password must contain at least 1 uppercase, 1 lowercase, 1 number, 1 special character and be at least 8 characters long")
    private String password;
}
