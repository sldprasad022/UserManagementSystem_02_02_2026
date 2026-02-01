package com.techpixe.exception;

public class OTPMismatchException extends RuntimeException
{
	public OTPMismatchException(String message)
	{
		super(message);
	}
}
