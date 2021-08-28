package com.faeem.authenticationservice.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.faeem.authenticationservice.common.I18nMessages;

abstract public class BaseService {
	
	@Autowired protected I18nMessages i18nMessages;

}
