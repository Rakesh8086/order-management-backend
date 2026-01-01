package com.notification.service.response;

import java.time.LocalDateTime;

import com.notification.service.entity.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
	@NotNull
    private Long id;
    @NotNull
    @Email
    private String recipientEmail;
    @NotNull
    private Long orderId;
    @NotNull
    private LocalDateTime timestamp;
    @NotNull
    private Status status; 
    private String errorMessage; 
}
