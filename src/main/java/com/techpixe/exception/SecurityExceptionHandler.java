package com.techpixe.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techpixe.dto.APIResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler
{

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Handle 401 Unauthorized
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)throws IOException
    {
        APIResponse<Object> apiResponse = APIResponse.failure(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please log in.");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    // Handle 403 Forbidden
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)throws IOException 
    {
    	APIResponse<Object> apiResponse = APIResponse.failure(HttpStatus.FORBIDDEN.value(), "You are unauthorized to access this resource.");

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}