package com.techpixe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techpixe.dto.APIResponse;
import com.techpixe.dto.UserResponseDto;
import com.techpixe.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Module",description = "APIs related to admin module")
public class AdminController 
{
	@Autowired
	private UserService userService;
	
	@Operation(summary = "Delete user by ID",description = "Allows admin to `delete` a user using `userId`")
	@DeleteMapping("/{userId}")
	public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable Long userId)
	{
		userService.deleteUser(userId);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User deleted successfully", null));			
	}
	
	@Operation(summary = "Get all users with pagination",description = "`Retrieve paginated list of users` for admin view")
	@GetMapping("/paginated-users")
	public ResponseEntity<APIResponse<Page<UserResponseDto>>> getAllPaginatedUsers(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size)
	{
		Page<UserResponseDto> result = userService.getUsersWithPagination(page, size);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User data retrieved successfully", result));	
	}
}
