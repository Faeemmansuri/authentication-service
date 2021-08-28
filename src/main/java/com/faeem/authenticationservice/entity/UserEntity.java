package com.faeem.authenticationservice.entity;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {
    
	private static final long serialVersionUID = -2859405837588960096L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column(unique = true)
    private String email;
    
    @Column(columnDefinition="tinyint(1) DEFAULT '0'")
    private boolean emailVerified;
    
    @CreatedDate
    private Date createdOn;
    
}
