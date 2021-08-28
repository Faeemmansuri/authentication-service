package com.faeem.authenticationservice.common.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.WebRequest;

public class RestUtil {
	
	public static final TimeZone DEFAULT_TIMEZONE_FALLBACK = TimeZone.getTimeZone("UTC");
	
	public static final Locale DEFAULT_LOCALE_FALLBACK = Locale.US;

	private RestUtil() {}
	
	public static Locale extractLocale(WebRequest request, String defaultLocale) {
		String languageHeader = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
		return convertToLocale(languageHeader, defaultLocale);
	}

	private static Locale convertToLocale(String acceptLanguage, String defaultLocale) {
		try {
			return Objects.nonNull(acceptLanguage) 
					? Locale.forLanguageTag(acceptLanguage) : DEFAULT_LOCALE_FALLBACK;
		} catch (RuntimeException re) {
			return DEFAULT_LOCALE_FALLBACK;
		}
	}
}