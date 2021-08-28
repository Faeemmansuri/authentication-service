package com.faeem.authenticationservice.verification.service;

import java.util.Map;

import com.faeem.authenticationservice.entity.UserEntity;
import com.faeem.authenticationservice.enums.VerificationType;

public interface VerificationService {

	String generateToken(UserEntity user, VerificationType verificationType);
	
	Map<String, Object> verifyToken(String token);
	
}
