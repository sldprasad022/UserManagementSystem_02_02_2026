package com.techpixe.exception;

public class UserAccountDeactivatedException extends RuntimeException
{
	public UserAccountDeactivatedException(String message)
	{
		super(message);
	}
}
