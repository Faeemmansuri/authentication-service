package com.faeem.authenticationservice.request;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterRequest implements Serializable {

	private static final long serialVersionUID = 3879504587845489887L;
	
	@NotBlank
	private String username;

	@NotBlank
	@Size(min = 8, max = 30)
	private String password;
	
	@NotBlank
	private String email;	

}
