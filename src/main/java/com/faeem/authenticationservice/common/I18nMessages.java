package com.faeem.authenticationservice.common;

import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.faeem.authenticationservice.common.utils.RestUtil;

@Component
public class I18nMessages {
	
	private final MessageSource messageSource;
	
	public I18nMessages(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public String get(String key, Locale locale, Object ...args) {
		if(Objects.isNull(locale)) {
			locale = RestUtil.DEFAULT_LOCALE_FALLBACK;
		}
		if(StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("Message key must not be blank/null.");
		}
		return messageSource.getMessage(key, args, locale);
	}
	
	public String get(String key, Object ...args) {
		Locale locale = RestUtil.DEFAULT_LOCALE_FALLBACK;	
		if(StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("Message key must not be blank/null.");
		}
		return messageSource.getMessage(key, args, locale);
	}

}
