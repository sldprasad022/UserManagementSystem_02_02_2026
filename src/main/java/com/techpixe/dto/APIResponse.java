package com.techpixe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T>
{
	private boolean success;
	
	private int statusCode;
	
	private String message;
	
	private T data;
	
	private T errors;
	
	public static <T> APIResponse<T> success(int statusCode,String message,T data)
	{
		return new APIResponse<T>(true, statusCode, message, data, null);
	}
	
	public static <T> APIResponse<T> failure(int statusCode,String message)
	{
		return new APIResponse<>(false,statusCode,message,null,null);
	}
	
	public static <T> APIResponse<T> failure(int statusCode,String message,T errors)
	{
		return new APIResponse<>(false,statusCode,message,null,errors);
	}
}
