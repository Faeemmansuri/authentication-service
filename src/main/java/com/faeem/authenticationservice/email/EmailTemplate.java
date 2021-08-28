package com.faeem.authenticationservice.email;

import java.util.List;
import java.util.Objects;

import org.thymeleaf.context.Context;

import lombok.Data;

@Data
public class EmailTemplate {
	
	private String from;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private List<Objects> attachments;
	private Context props;
	private String templateName;
//	private String emailBody;
	
}
