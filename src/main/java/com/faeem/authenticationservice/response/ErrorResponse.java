package com.faeem.authenticationservice.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	
	@JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Date timestamp;
    private String errorMessage;
    private List<String> errorMessages;
    private String details;
}

