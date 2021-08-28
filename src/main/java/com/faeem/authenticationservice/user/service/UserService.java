package com.faeem.authenticationservice.user.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.faeem.authenticationservice.request.LoginRequest;
import com.faeem.authenticationservice.request.RegisterRequest;
import com.faeem.authenticationservice.request.ResetPasswordRequest;
import com.faeem.authenticationservice.response.LoginResponse;

public interface UserService extends UserDetailsService {

	void registerUser(RegisterRequest request);
	
	LoginResponse loginUser(LoginRequest request);

	void forgetPassword(String email);

	void resetPassword(ResetPasswordRequest request);
	
}
