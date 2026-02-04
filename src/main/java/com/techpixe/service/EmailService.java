package com.techpixe.service;

import com.techpixe.entity.User;

public interface EmailService 
{
	void sendRegistrationOtp(String email, String otp, int expiryMinutes);

    void sendForgotPasswordOtp(User user, String otp, int expiryMinutes);
}
