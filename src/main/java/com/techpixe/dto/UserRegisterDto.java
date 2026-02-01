package com.techpixe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto 
{
	@NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Username must contain only letters and spaces")
    private String userName;
	
	@NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 14, message = "Password must be between 6 and 14 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,14}$",
    		 message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character"
    		)
    private String password;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 14, message = "Password must be between 6 and 14 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,14}$",
    		 message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character"
    		)
    private String confirmPassword;
}
