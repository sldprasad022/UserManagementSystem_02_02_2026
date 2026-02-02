package com.techpixe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDto
{
	@NotBlank(message = "current password is required")
    private String currentPassword;
	
    @NotBlank(message = "New Password is required")
    @Size(min = 6, max = 14, message = "Password must be between 6 and 14 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,14}$",
    		 message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character")
    private String newPassword;
    
    @NotBlank(message = "Confirm Password is required")
    @Size(min = 6, max = 14, message = "Password must be between 6 and 14 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,14}$",
    		 message = "Password must contain at least one uppercase, one lowercase, one digit, and one special character")
    private String confirmPassword;
}
