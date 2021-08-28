package com.faeem.authenticationservice.email;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailUtil {

	@Autowired private JavaMailSender mailSender;
	@Autowired private TemplateEngine templateEngine;

	@Async
	public void sendEmail(EmailTemplate template) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, 
					MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			helper.setFrom(template.getFrom());
			helper.setTo(template.getTo());
			helper.setSubject(template.getSubject());

			if (StringUtils.isNotBlank(template.getCc())) {
				helper.setCc(template.getCc());
			}

			if (StringUtils.isNotBlank(template.getBcc())) {
				helper.setBcc(template.getBcc());
			}

			String htmlBody = templateEngine.process(template.getTemplateName(), template.getProps());
			helper.setText(htmlBody, true);

			mailSender.send(message);

		} catch (MessagingException ex) {
			log.error(ex.getMessage(), ex);
		}
	}

}
