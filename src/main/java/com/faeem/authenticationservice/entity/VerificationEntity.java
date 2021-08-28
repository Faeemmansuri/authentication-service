package com.faeem.authenticationservice.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.faeem.authenticationservice.enums.VerificationType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "verification_token")
public class VerificationEntity implements Serializable {

	private static final long serialVersionUID = -2859405837588960096L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "confirmation_token")
	private String confirmationToken;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "verification_type")
	private VerificationType verificationType = VerificationType.REGISTER;

	@OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private UserEntity user;

	@CreatedDate
	private Date createdOn;

	public VerificationEntity(UserEntity user) {
		this.user = user;
		createdOn = new Date();
		confirmationToken = UUID.randomUUID().toString();
	}

}
