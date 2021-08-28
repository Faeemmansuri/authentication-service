package com.faeem.authenticationservice.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResetPasswordRequest implements Serializable{
	 
	private static final long serialVersionUID = -8279725026309313483L;

	private String password;
	
}
