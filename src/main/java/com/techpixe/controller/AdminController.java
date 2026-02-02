package com.techpixe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techpixe.dto.APIResponse;
import com.techpixe.dto.UserResponseDto;
import com.techpixe.dto.UserSummaryCountDto;
import com.techpixe.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Module",description = "API's related to admin dashboard")
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
	
	@Operation(summary = "Get user statistics",description = "Retrieves `total`, `active`, and `inactive user counts` for `dashboard`")	
	@GetMapping("/users/statistics")
	public ResponseEntity<APIResponse<UserSummaryCountDto>> getUserStatusCount()
	{
		UserSummaryCountDto result = userService.getUserStatusCount();
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "User summary statistics retrieved successfully", result));
	}
	
	@Operation(summary = "Activate user and Deactivate user", description = "`Activate user` and `Deactivate user`")
	@PostMapping("/{userId}/toggle-user-status")
	public ResponseEntity<APIResponse<Void>> toggleUsersStatus(@PathVariable Long userId)
	{
		String result = userService.toggleUserStatus(userId);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), result, null));
	}
	
	@Operation(summary = "Search By Username or Email", description = "Search by `username` or `email`")
	@GetMapping("/users/search")
	public ResponseEntity<APIResponse<Page<UserResponseDto>>> searchUsersByUserName(
																@RequestParam String userName,
																@RequestParam(defaultValue = "0") int page,
																@RequestParam(defaultValue = "10") int size)
	{
		Page<UserResponseDto> result = userService.searchUsers(userName, page, size);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "Users data retrieved successfully based on search (user name)", result));
	}
	
	@GetMapping("/users/active")
	@Operation(summary = "Active user's data with Pagination", description = "`Active user's data` with `Pagination`")
	public ResponseEntity<APIResponse<Page<UserResponseDto>>> getActiveUsers(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size)
	{
		Page<UserResponseDto> result = userService.getActiveUsers(page, size);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "Active Users data retrieved successfully", result));
	}
	
	@Operation(summary = "Inactive user's data with Pagination", description = "`Inactive user's data` with `Pagination`")
	@GetMapping("/users/inactive")
	public ResponseEntity<APIResponse<Page<UserResponseDto>>> getInactiveUsers(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size)
	{
		Page<UserResponseDto> result = userService.getInActiveUsers(page, size);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "Inactive Users data retrieved successfully", result));
	}
}
