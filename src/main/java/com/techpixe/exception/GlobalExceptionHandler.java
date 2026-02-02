package com.techpixe.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.techpixe.dto.APIResponse;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
	// 1 Validation
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<APIResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) 
	{

		List<Map<String, String>> errorList = new ArrayList<>();

		for (FieldError error : ex.getBindingResult().getFieldErrors())
		{
			Map<String, String> err = new HashMap<>();
			err.put("field", error.getField());
			err.put("message", error.getDefaultMessage());
			errorList.add(err);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(APIResponse.failure(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errorList));
	}

	// 2️ Invalid HTTP method (e.g., POST used instead of GET)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<APIResponse<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) 
	{
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(APIResponse.failure(HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage()));
	}

	// 3️ Invalid URL or endpoint
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<APIResponse<Object>> handleNotFound(NoHandlerFoundException ex) 
	{
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(APIResponse.failure(HttpStatus.NOT_FOUND.value(), "URL does not exist"));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<APIResponse<Object>> handleResponseStatusException(ResponseStatusException ex)
	{
		return ResponseEntity.status(ex.getStatusCode())
				.body(APIResponse.failure(ex.getStatusCode().value(), ex.getReason()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<APIResponse<Object>> handleGlobalException(Exception ex) 
	{
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse
				.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong.  Please try again later."));
	}

	// user not found email already exists exception
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<APIResponse<Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) 
	{
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(APIResponse.failure(HttpStatus.CONFLICT.value(), ex.getMessage()));
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<APIResponse<Object>> handleUserNotFound(UserNotFoundException ex) 
	{
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(APIResponse.failure(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
	}

	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<APIResponse<Object>> handleOtpExpired(OtpExpiredException ex) 
	{
		return ResponseEntity.status(HttpStatus.GONE)
				.body(APIResponse.failure(HttpStatus.GONE.value(), ex.getMessage()));
	}

	@ExceptionHandler(OTPMismatchException.class)
	public ResponseEntity<APIResponse<Object>> handleOTPMismatch(OTPMismatchException ex)
	{
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(APIResponse.failure(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
	}
	
	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<APIResponse<Object>> handlePasswordMismatch(PasswordMismatchException ex)
	{
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(APIResponse.failure(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}
	
	@ExceptionHandler(EmailNotVerifiedException.class)
	public ResponseEntity<APIResponse<Object>> handleEmailNotVerified(EmailNotVerifiedException ex) 
	{
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(APIResponse.failure(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
	}
	
	
	@ExceptionHandler(OtpNotRequestedException.class)
	public ResponseEntity<APIResponse<Object>> handleOtpNotRequested(OtpNotRequestedException ex) 
	{
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(APIResponse.failure(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}
	
	@ExceptionHandler(UserAlreadyRegisteredException.class)
	public ResponseEntity<APIResponse<Object>> handleUserAlreadyRegistered(UserAlreadyRegisteredException ex) 
	{
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(APIResponse.failure(HttpStatus.CONFLICT.value(), ex.getMessage()));
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<APIResponse<Object>> handleAuthentication(AuthenticationException ex) 
	{
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(APIResponse.failure(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
	}
	
	
}
