package com.techpixe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techpixe.dto.APIResponse;
import com.techpixe.dto.EmailRegisterRequestDto;
import com.techpixe.dto.UserRegisterDto;
import com.techpixe.dto.VerifyEmailOtpDto;
import com.techpixe.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication API's",description = "APIs related to email verification, user registration, and authentication")
public class AuthController 
{
	@Autowired
	private UserService userService;
	
	@Operation(summary = "Send email OTP",description = "Sends `OTP` to the given `email` for `verification`")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "400", description = "Invalid email")
    }) 
	@PostMapping("/register/email")   
	public ResponseEntity<APIResponse<Void>> initiateEmailRegistration(@Valid @RequestBody EmailRegisterRequestDto emailRegisterRequestDto)
	{
		userService.initiateEmailRegistration(emailRegisterRequestDto);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "OTP sent to email successfully", null));
	}
	
	@Operation(summary = "Verify email OTP",description = "`Validates OTP` and marks `email as verified`")   
	@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
	        @ApiResponse(responseCode = "400", description = "Invalid OTP"),
	        @ApiResponse(responseCode = "410", description = "OTP expired")
	    })
	@PostMapping("/register/verify-email-otp")
	public ResponseEntity<APIResponse<Void>> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpDto verifyEmailOtpDto)
	{
		userService.verifyEmailOtp(verifyEmailOtpDto);
		return ResponseEntity.ok(APIResponse.success(HttpStatus.OK.value(), "Email verified successfully", null));
	}
	
	@PostMapping("/register")
	@Operation(summary = "Register user",description = "`Registers` a `new user` after `email verification`")
	@ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "403", description = "Email not verified"),
        @ApiResponse(responseCode = "409", description = "User already registered")
    })            
	public ResponseEntity<APIResponse<Void>> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto)
	{
		userService.registerUser(userRegisterDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(HttpStatus.CREATED.value(), "User registered successfully", null));
	}
	
}
