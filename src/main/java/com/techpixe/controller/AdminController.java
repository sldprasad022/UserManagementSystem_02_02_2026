package com.techpixe.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.techpixe.service.UserService;

public class AdminController 
{
	@Autowired
	private UserService userService;
}
