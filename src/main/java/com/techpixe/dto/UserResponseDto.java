package com.techpixe.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.techpixe.entity.User;
import com.techpixe.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto 
{
	private Long userId;

    private String userName;

    private String email;
    
    private Role role;
    
    private boolean isProfileCompleted;
    
    private boolean isActive;
    
    public static UserResponseDto fromEntity(User user)
    {
    	return new UserResponseDto(user.getUserId(),user.getUserName(),user.getEmail(),user.getRole(),user.isProfileCompleted(),user.isActive());
    }
    
    public static Page<UserResponseDto> fromEntityPage(Page<User> usersPage)
    {
    	List<UserResponseDto> data = usersPage.stream()
    										.map(UserResponseDto :: fromEntity)
    										.toList();
    	
    	return new PageImpl<>(data, usersPage.getPageable(),usersPage.getTotalElements());
    }
}
