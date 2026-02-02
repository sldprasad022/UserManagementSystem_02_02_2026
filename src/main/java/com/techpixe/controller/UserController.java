package com.techpixe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techpixe.dto.APIResponse;
import com.techpixe.dto.ChangePasswordRequestDto;
import com.techpixe.dto.UserResponseDto;
import com.techpixe.dto.UserUpdateRequestDto;
import com.techpixe.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Module",description = "APIs related to user module")
public class UserController 
{
	@Autowired
	private UserService userService;
	
	
	@Operation(summary = "Get user details",description = "Fetch the `user information` using `userId`")
	@GetMapping("/{userId}")
	public ResponseEntity<APIResponse<UserResponseDto>> getUser(@PathVariable Long userId)
	{
		UserResponseDto result = userService.getUser(userId);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User data retrieved successfully", result));
	}
	
	@Operation(summary = "Update user profile",description = "`Update user details` such as `name`")
	@PatchMapping("/{userId}")
	public ResponseEntity<APIResponse<Void>> updateUser(@PathVariable Long userId,@Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto)
	{
		userService.updateUser(userId, userUpdateRequestDto);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User updated successfully", null));		
	}
	
	@Operation(summary = "Change password",description = "Allows user to `change password` by providing `old` and `new password`")
	@PostMapping("/change-password/{userId}")
	public ResponseEntity<APIResponse<Void>> changePassword(@PathVariable Long userId,@Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto)
	{
		userService.changePassword(userId, changePasswordRequestDto);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "Password changed successfully", null));	
	}
}
