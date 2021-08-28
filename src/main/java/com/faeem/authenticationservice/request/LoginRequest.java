package com.faeem.authenticationservice.request;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequest implements Serializable{
    
	private static final long serialVersionUID = 7243494106876385652L;

	@NotBlank
    private String username;

    @NotBlank
    private String password;

}