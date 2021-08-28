package com.faeem.authenticationservice.controller;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.faeem.authenticationservice.request.LoginRequest;
import com.faeem.authenticationservice.request.RegisterRequest;
import com.faeem.authenticationservice.request.ResetPasswordRequest;
import com.faeem.authenticationservice.user.service.UserService;
import com.faeem.authenticationservice.verification.service.VerificationService;

@RestController
public class AuthenticationController {
	
	@Autowired UserService userService;
	@Autowired VerificationService verificationService; 
	
	@GetMapping("/login")
	public ResponseEntity<?> loginUserGEt() {
		return ResponseEntity.ok("Authentication");
	}
	
	@GetMapping("/sayHello")
	public ResponseEntity<?> sayHello() {
		return ResponseEntity.ok("sayHello");
	}

	
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(userService.loginUser(request));
	}
	
	@PostMapping("/register")
	public ResponseEntity<Void> registerUser(@Valid @RequestBody RegisterRequest request) {
		userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/verify")
	public ResponseEntity<Map> verifyEmail(@RequestParam String token) {
		return ResponseEntity.ok(verificationService.verifyToken(token));
	}
	
	@PostMapping("/forget-password")
	public ResponseEntity<Void> forgetPassword(@Valid @RequestParam @NotBlank String email) {
		userService.forgetPassword(email);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<Void> forgetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		userService.resetPassword(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}
