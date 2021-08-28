package com.faeem.authenticationservice.verification.service;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.faeem.authenticationservice.entity.UserEntity;
import com.faeem.authenticationservice.entity.VerificationEntity;
import com.faeem.authenticationservice.enums.VerificationType;
import com.faeem.authenticationservice.exception.BadRequestException;
import com.faeem.authenticationservice.jwt.utils.JwtUtils;
import com.faeem.authenticationservice.repository.UserRepository;
import com.faeem.authenticationservice.repository.VerificationRepository;
import com.faeem.authenticationservice.service.BaseService;

@Service
public class VerificationServiceImpl extends BaseService implements VerificationService {
	
	@Autowired private VerificationRepository verificationRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private JwtUtils jwtUtils;

	@Override
	public String generateToken(UserEntity user, VerificationType verificationType) {
		VerificationEntity verification = new VerificationEntity(user);
		verification.setVerificationType(verificationType);
		verification = verificationRepository.save(verification);
		return verification.getConfirmationToken();
	}

	@Override
	public Map<String, Object> verifyToken(String token) {
		if(StringUtils.isEmpty(token)) {
			throw new BadRequestException(i18nMessages.get("email.token.invalid"));
		}
		
		VerificationEntity verificationEntity = verificationRepository.findByConfirmationToken(token);
		if(Objects.isNull(verificationEntity)) {
			throw new BadRequestException(i18nMessages.get("email.token.invalid"));
		}
		
		UserEntity userEntity = verificationEntity.getUser();
		
		if(verificationEntity.getVerificationType().equals(VerificationType.REGISTER)) {
			verifyEmailToken(userEntity);
			verificationRepository.delete(verificationEntity);
			return Map.of("status", "success");
		} else if (verificationEntity.getVerificationType().equals(VerificationType.FORGET_PASSWORD)) {
			verificationRepository.delete(verificationEntity);
			String resetToken = jwtUtils.generateTokenForPasswordReset(userEntity); 
			return Map.of("status", "success", "token", resetToken);
		}
		return Collections.emptyMap();
	}
	
	private void verifyEmailToken(UserEntity userEntity) {
		userEntity.setEmailVerified(Boolean.TRUE);
		userRepository.save(userEntity);
	}

}
