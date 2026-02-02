package com.techpixe.exception;

public class IncorrectOldPasswordException extends RuntimeException
{
	public IncorrectOldPasswordException(String message)
	{
		super(message);
	}
}
