package com.techpixe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techpixe.dto.APIResponse;
import com.techpixe.dto.UserResponseDto;
import com.techpixe.dto.UserUpdateRequestDto;
import com.techpixe.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Module",description = "APIs related to user module")
public class UserController 
{
	@Autowired
	private UserService userService;
	
	@GetMapping("/{userId}")
	public ResponseEntity<APIResponse<UserResponseDto>> getUser(@PathVariable Long userId)
	{
		UserResponseDto result = userService.getUser(userId);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User data retrieved successfully", result));
	}
	
	@GetMapping("/paginated-users")
	public ResponseEntity<APIResponse<Page<UserResponseDto>>> getAllPaginatedUsers(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size)
	{
		Page<UserResponseDto> result = userService.getUsersWithPagination(page, size);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User data retrieved successfully", result));	
	}
	
	@PatchMapping("/update/{userId}")
	public ResponseEntity<APIResponse<Void>> updateUser(@PathVariable Long userId,@Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto)
	{
		userService.updateUser(userId, userUpdateRequestDto);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User updated successfully", null));		
	}
	
	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable Long userId)
	{
		userService.deleteUser(userId);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User deleted successfully", null));			
	}
	
	
}
