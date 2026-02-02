package com.techpixe.service;

import org.springframework.data.domain.Page;

import com.techpixe.dto.ChangePasswordRequestDto;
import com.techpixe.dto.EmailRegisterRequestDto;
import com.techpixe.dto.ForgotPasswordDto;
import com.techpixe.dto.ForgotPasswordOtpRequestDto;
import com.techpixe.dto.LoginRequestDto;
import com.techpixe.dto.LoginResponseDto;
import com.techpixe.dto.UserRegisterDto;
import com.techpixe.dto.UserResponseDto;
import com.techpixe.dto.UserUpdateRequestDto;
import com.techpixe.dto.VerifyEmailOtpDto;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService 
{
	//------------------------------
	void registerUser(UserRegisterDto userRegisterDto);
	
	void initiateEmailRegistration(EmailRegisterRequestDto emailRegisterRequestDto);
	
	void verifyEmailOtp(VerifyEmailOtpDto verifyEmailOtpDto);
	
	//------------------------------
	UserResponseDto getUser(Long userId);
	
	Page<UserResponseDto> getUsersWithPagination(int page, int size);
	
	void updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto);
	
	void deleteUser(Long userId);
	//-------------------------------
	LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest);
	
	void forgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto);
	
	void forgotPassword(ForgotPasswordDto forgotPasswordDto);
	
	void resendForgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto);
	
	void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto);
	
	

	
}
