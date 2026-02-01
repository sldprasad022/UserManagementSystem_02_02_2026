package com.techpixe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyEmailOtpDto 
{
	@NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
	
	@NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$",message = "OTP must be exactly 6 digits")
    private String otp;
}
