package com.techpixe.serviceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techpixe.dto.EmailRegisterRequestDto;
import com.techpixe.dto.UserRegisterDto;
import com.techpixe.dto.UserResponseDto;
import com.techpixe.dto.UserUpdateRequestDto;
import com.techpixe.dto.VerifyEmailOtpDto;
import com.techpixe.entity.User;
import com.techpixe.enums.Role;
import com.techpixe.exception.EmailAlreadyExistsException;
import com.techpixe.exception.EmailNotVerifiedException;
import com.techpixe.exception.OTPMismatchException;
import com.techpixe.exception.OtpExpiredException;
import com.techpixe.exception.OtpNotRequestedException;
import com.techpixe.exception.PasswordMismatchException;
import com.techpixe.exception.UserAlreadyRegisteredException;
import com.techpixe.exception.UserNotFoundException;
import com.techpixe.repository.UserRepository;
import com.techpixe.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String fromMail;

	private static final int OTP_VALIDITY_MINUTES = 2;

	public static String generateOTP() 
	{
		// Random random = new Random();
		SecureRandom random = new SecureRandom();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}


	
	@Override
	public void initiateEmailRegistration(EmailRegisterRequestDto emailRegisterRequestDto)
	{

	    String email = emailRegisterRequestDto.getEmail();
	    
	    String generatedOTP = generateOTP();

	    Optional<User> existingUserOpt = userRepository.findByEmail(email);
	    User user;
	    
	    if (existingUserOpt.isPresent()) 
	    {
	        user = existingUserOpt.get();
	        if (user.isEmailVerified())
	        {
	            throw new EmailAlreadyExistsException("Email already exists and verified");
	        }
	        user.setOtp(passwordEncoder.encode(generatedOTP));
	        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
	    } 
	    else 
	    {
	        // New user
	        user = new User();
	        user.setEmail(email);
	        user.setOtp(passwordEncoder.encode(generatedOTP));
	        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
	        user.setCreatedAt(LocalDateTime.now());
	        user.setEmailVerified(false);
	    }
	    userRepository.save(user);
	    sendEmailOtp(email, generatedOTP);
	}
	
	 public void sendEmailOtp(String toEmail, String otp)
	 {
	        try 
	        {
	            MimeMessage message = javaMailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
	            helper.setFrom(fromMail);
	            helper.setTo(toEmail);
	            helper.setSubject("OTP for Email Verification");
	            // 👉 Template method call
	            helper.setText(buildEmailOtpTemplate(toEmail, otp), true);

	            javaMailSender.send(message);
	        } 
	        catch (MessagingException e) 
	        {
	            throw new RuntimeException("Failed to send OTP email", e);
	        }
	    }

	    private String buildEmailOtpTemplate(String email, String otp)
	    {

	        return String.format("""
	            <!DOCTYPE html>
	            <html lang="en">
	            <head>
	                <meta charset="UTF-8">
	                <style>
	                    body {
	                        font-family: Arial, sans-serif;
	                        background-color: #f4f6f8;
	                        padding: 20px;
	                    }
	                    .container {
	                        max-width: 600px;
	                        margin: auto;
	                        background: #ffffff;
	                        padding: 20px;
	                        border-radius: 8px;
	                        box-shadow: 0 0 10px rgba(0,0,0,0.1);
	                    }
	                    .header {
	                        text-align: center;
	                        color: #2e7d32;
	                    }
	                    .otp {
	                        font-size: 26px;
	                        font-weight: bold;
	                        color: #e91e63;
	                        text-align: center;
	                        margin: 20px 0;
	                    }
	                    .footer {
	                        margin-top: 30px;
	                        font-size: 14px;
	                        color: #777;
	                        text-align: center;
	                    }
	                </style>
	            </head>
	            <body>
	                <div class="container">
	                    <h2 class="header">Email Verification</h2>
	                    <p>Hello,</p>
	                    <p>We received a request to verify the email address:</p>
	                    <p><b>%s</b></p>

	                    <p>Please use the below One-Time Password (OTP):</p>
	                    <div class="otp">%s</div>

	                    <p>This OTP is valid for <b>2 minutes</b>.</p>
	                    <p>If you did not request this, please ignore this email.</p>

	                    <div class="footer">
	                        <p>— Techpixe</p>
	                    </div>
	                </div>
	            </body>
	            </html>
	        """, email, otp);
	    }
	


	@Override
	public void verifyEmailOtp(VerifyEmailOtpDto verifyEmailOtpDto) 
	{
		User user = userRepository.findByEmail(verifyEmailOtpDto.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
		        	
		if (user.getOtp()==null || user.getOtpExpiry()==null)
		{
			throw new OtpNotRequestedException("No OTP request found");
		}
		
		if (user.getOtpExpiry().isBefore(LocalDateTime.now()))
		{
			throw new OtpExpiredException("OTP has expired");
		}
		
		if (!passwordEncoder.matches(verifyEmailOtpDto.getOtp(), user.getOtp())) 
		{
			throw new OTPMismatchException(" Invalid OTP. Please check and try again.");
		}
		
		user.setEmailVerified(true);
		user.setOtp(null);
		user.setOtpExpiry(null);
		userRepository.save(user);

	}

	@Override
	public void registerUser(UserRegisterDto userRegisterDto) 
	{
	    User user = userRepository.findByEmail(userRegisterDto.getEmail()).orElseThrow(() -> new UserNotFoundException("Email not found"));
	        
	    if (!user.isEmailVerified())
	    {
	        throw new EmailNotVerifiedException("Email is not verified.Please verify email first");       
	    }
	    
	    if (user.isProfileCompleted()) 
	    {
	    	throw new UserAlreadyRegisteredException("User already registered");   
		}
	  
	    if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword()))
	    {
			throw new PasswordMismatchException("Password and Confirm Password do not match");
		}
	    	
	    user.setUserName(userRegisterDto.getUserName());
	    user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
	    
	    user.setRole(Role.ROLE_USER);
	    user.setProfileCompleted(true);
	    user.setActive(true);
	    user.setUpdatedAt(LocalDateTime.now());

	    userRepository.save(user);
	}
	
	
	
//-----------------
	@Override
	public UserResponseDto getUser(Long userId)
	{
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		return UserResponseDto.fromEntity(user);
	}

	@Override
	public Page<UserResponseDto> getUsersWithPagination(int page, int size)
	{
		Pageable pageable = PageRequest.of(page, size);
		Page<User> response = userRepository.findAll(pageable);
		return UserResponseDto.fromEntityPage(response);
	}

	@Override
	public void updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto) 
	{
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not Found"));

		if (userUpdateRequestDto.getUserName() != null)
		{
			user.setUserName(userUpdateRequestDto.getUserName());
		}

		userRepository.save(user);
	}

	@Override
	public void deleteUser(Long userId)
	{
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		userRepository.delete(user);
	}

}
