package com.faeem.authenticationservice.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.faeem.authenticationservice.entity.UserEntity;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {
	
	UserEntity findByUsername(String username);
	
	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByUsernameOrEmail(String username, String email);
	
}
