package com.faeem.authenticationservice.repository;

import org.springframework.stereotype.Repository;

import com.faeem.authenticationservice.entity.VerificationEntity;

@Repository
public interface VerificationRepository extends BaseRepository<VerificationEntity, Long> {
	
	VerificationEntity findByConfirmationToken(String token);

}
