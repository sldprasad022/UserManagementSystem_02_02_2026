package com.techpixe.serviceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techpixe.dto.ChangePasswordRequestDto;
import com.techpixe.dto.EmailRegisterRequestDto;
import com.techpixe.dto.ForgotPasswordDto;
import com.techpixe.dto.ForgotPasswordOtpRequestDto;
import com.techpixe.dto.LoginRequestDto;
import com.techpixe.dto.LoginResponseDto;
import com.techpixe.dto.UserRegisterDto;
import com.techpixe.dto.UserResponseDto;
import com.techpixe.dto.UserSummaryCountDto;
import com.techpixe.dto.UserUpdateRequestDto;
import com.techpixe.dto.VerifyEmailOtpDto;
import com.techpixe.entity.LoginAudit;
import com.techpixe.entity.User;
import com.techpixe.enums.Role;
import com.techpixe.exception.AuthenticationException;
import com.techpixe.exception.EmailAlreadyExistsException;
import com.techpixe.exception.EmailNotVerifiedException;
import com.techpixe.exception.IncorrectOldPasswordException;
import com.techpixe.exception.OTPMismatchException;
import com.techpixe.exception.OtpExpiredException;
import com.techpixe.exception.OtpNotRequestedException;
import com.techpixe.exception.PasswordMismatchException;
import com.techpixe.exception.UserAlreadyRegisteredException;
import com.techpixe.exception.UserNotFoundException;
import com.techpixe.notification.EmailOTPVerificationForRegistration;
import com.techpixe.notification.EmailTemplateForForgotPassword;
import com.techpixe.repository.LoginAuditRepository;
import com.techpixe.repository.UserRepository;
import com.techpixe.service.UserService;
import com.techpixe.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LoginAuditRepository loginAuditRepository;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailOTPVerificationForRegistration emailOTPVerificationForRegistration;
	
	@Autowired
	private EmailTemplateForForgotPassword emailTemplateForForgotPassword;

	private static final int OTP_VALIDITY_MINUTES = 2;
	
	private static final int MAX_ATTEMPTS = 5;
	
    private static final int LOCK_DURATION_MINUTES = 10;
    

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
	    
	    emailOTPVerificationForRegistration.sendEmailOtp(email, generatedOTP);
	}
	
	@Override
	public void verifyEmailOtp(VerifyEmailOtpDto verifyEmailOtpDto) 
	{
		User user = userRepository.findByEmail(verifyEmailOtpDto.getEmail()).orElseThrow(() -> new UserNotFoundException("Email not found"));
		        	
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
		Pageable pageable = PageRequest.of(page, size,Sort.by("userId").ascending());
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

	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest)
	{

	    String email = loginRequestDto.getEmail();
	    String password = loginRequestDto.getPassword();

	    LoginAudit audit = new LoginAudit();
	    audit.setEmail(email);
	    audit.setAttemptTime(LocalDateTime.now());
	    audit.setIpAddress(httpServletRequest.getRemoteAddr());
	    audit.setUserAgent(httpServletRequest.getHeader("User-Agent"));

	    Optional<User> optionalUser = userRepository.findByEmail(email);
	    if (optionalUser.isEmpty())
	    {
	        audit.setSuccess(false);
	        loginAuditRepository.save(audit);
	        throw new AuthenticationException("Invalid credentials.");
	    }

	    User user = optionalUser.get();
	    if (user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) 
	    {
	        audit.setSuccess(false);
	        loginAuditRepository.save(audit);
	        //throw new AuthenticationException("Account is locked until " + user.getAccountLockedUntil().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	        throw new AuthenticationException("Account is locked. Try again later.");
	    }	    
	    if (!passwordEncoder.matches(password, user.getPassword()))
	    {
	        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
	        if (user.getFailedLoginAttempts() >= MAX_ATTEMPTS) 
	        {
	            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
	        }
	        userRepository.save(user);

	        audit.setSuccess(false);
	        loginAuditRepository.save(audit);
	        throw new AuthenticationException("Invalid credentials.");
	    }

	    // Successful login
	    user.setFailedLoginAttempts(0);
	    user.setAccountLockedUntil(null);
	    user.setLastLogin(LocalDateTime.now());
	    
	    userRepository.save(user);
	    
	    audit.setSuccess(true);
	    loginAuditRepository.save(audit);

	    String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
	    UserResponseDto userResponseDto = UserResponseDto.fromEntity(user);

	    return new LoginResponseDto(token, userResponseDto);
	}

	@Override
	public void forgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto) 
	{
		User user = userRepository.findByEmail(forgotPasswordOtpRequestDto.getEmail())
	            .orElseThrow(() -> new UserNotFoundException("Email not found"));

	    String otp = generateOTP();
	    user.setOtp(passwordEncoder.encode(otp));
	    user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));

	    userRepository.save(user);

	    emailTemplateForForgotPassword.sendForgotPasswordOtp(user, otp);
		
	}
	
	@Override
	public void forgotPassword(ForgotPasswordDto forgotPasswordDto)
	{

	    User user = userRepository.findByEmail(forgotPasswordDto.getEmail())
	            .orElseThrow(() -> new UserNotFoundException("Email not found"));

	    if (user.getOtp() == null || user.getOtpExpiry() == null) 
	    {
	    	throw new OtpNotRequestedException("No OTP request found");
	    }
	    if (user.getOtpExpiry().isBefore(LocalDateTime.now())) 
	    {
	        throw new OtpExpiredException("OTP expired");
	    }
	    if (!passwordEncoder.matches(forgotPasswordDto.getOtp(), user.getOtp())) 
	    {
	    	throw new OTPMismatchException(" Invalid OTP. Please check and try again.");
	    }

	    user.setPassword(passwordEncoder.encode(forgotPasswordDto.getNewPassword()));
	    user.setOtp(null);
	    user.setOtpExpiry(null);

	    userRepository.save(user);
	}

	@Override
	public void resendForgotPasswordSendOTP(ForgotPasswordOtpRequestDto forgotPasswordOtpRequestDto) 
	{
		User user = userRepository.findByEmail(forgotPasswordOtpRequestDto.getEmail())
	            .orElseThrow(() -> new UserNotFoundException("Email not found"));

	    String otp = generateOTP();
	    user.setOtp(passwordEncoder.encode(otp));
	    user.setOtpExpiry(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));

	    userRepository.save(user);

	    emailTemplateForForgotPassword.sendForgotPasswordOtp(user, otp);
	}

	@Override
	public void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto) 
	{
		User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found"));
		
		if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) 
		{
			throw new PasswordMismatchException("Password and Confirm Password do not match");
		}
		if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(),user.getPassword()))
		{
			throw new IncorrectOldPasswordException("Invalid Credentials");
		}
		
		user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
		userRepository.save(user);
	}

	@Override
	public UserSummaryCountDto getUserStatusCount() 
	{
		UserSummaryCountDto userSummaryCountDto = new UserSummaryCountDto();
		userSummaryCountDto.setTotalUsersCount(userRepository.count());
		userSummaryCountDto.setTotalActiveUsersCount(userRepository.countByIsActiveTrue());
		userSummaryCountDto.setTotalInactiveUsersCount(userRepository.countByIsActiveFalse());
		return userSummaryCountDto;
	}

	@Override
	public String toggleUserStatus(Long userId) 
	{
		User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found"));
		user.setActive(!user.isActive());
		userRepository.save(user);
		
		if (user.isActive()) 
		{
			return "User activated successfully";
		}
		else
		{
			return "User deactivated successfully";
		}
	}

	@Override
	public Page<UserResponseDto> searchUsers(String keyword, int page, int size) 
	{
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
		Page<User> result = userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
		return UserResponseDto.fromEntityPage(result);
	}

	@Override
	public Page<UserResponseDto> getActiveUsers(int page, int size)
	{
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
		Page<User> result = userRepository.findByIsActive(true, pageable);
		return UserResponseDto.fromEntityPage(result);
	}

	@Override
	public Page<UserResponseDto> getInActiveUsers(int page, int size) 
	{
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
		Page<User> result = userRepository.findByIsActive(false, pageable);
		return UserResponseDto.fromEntityPage(result);
	}

	


}
