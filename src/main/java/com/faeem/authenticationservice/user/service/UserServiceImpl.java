package com.faeem.authenticationservice.user.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;

import com.faeem.authenticationservice.email.EmailTemplate;
import com.faeem.authenticationservice.email.EmailUtil;
import com.faeem.authenticationservice.entity.UserEntity;
import com.faeem.authenticationservice.enums.VerificationType;
import com.faeem.authenticationservice.exception.BadRequestException;
import com.faeem.authenticationservice.jwt.utils.JwtUtils;
import com.faeem.authenticationservice.repository.UserRepository;
import com.faeem.authenticationservice.request.LoginRequest;
import com.faeem.authenticationservice.request.RegisterRequest;
import com.faeem.authenticationservice.request.ResetPasswordRequest;
import com.faeem.authenticationservice.response.LoginResponse;
import com.faeem.authenticationservice.service.BaseService;
import com.faeem.authenticationservice.verification.service.VerificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl extends BaseService implements UserService {
	
	private static final String PASSWORD_VALIDATION_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*(\\W|_)).{8,}$";
	private static final String EMAIL_VALIDATION_PATTERN = "^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$";
	private static final String ROLE_USER = "ROLE_USER"; 
	
	@Autowired private JwtUtils jwtUtils;
	@Autowired private UserRepository userRepository; 
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private VerificationService verificationService; 
	@Autowired private EmailUtil emailUtil;
	
	@Value("spring.mail.username") private String mailFrom;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByUsername(username);
		if(Objects.isNull(user)) {
			throw new UsernameNotFoundException("Invalid username/password");
		}
		List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER));
		return new User(user.getUsername(), user.getPassword(), authorities);
	}

	@Override
	public void registerUser(RegisterRequest request) {
		boolean isValidPassword = Pattern.matches(PASSWORD_VALIDATION_PATTERN, request.getPassword());
		if(!isValidPassword) {
			throw new BadRequestException(i18nMessages.get("rest.error.invalid.password"));
		}
		
		boolean isValidEmail = Pattern.matches(EMAIL_VALIDATION_PATTERN, request.getEmail());
		if(!isValidEmail) {
			throw new BadRequestException(i18nMessages.get("rest.error.invalid.email"));
		}
		
		Optional<UserEntity> userOptional = userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail());
		if(userOptional.isPresent()) {
			throw new BadRequestException(i18nMessages.get("rest.error.user-pass.exists"));
		}
		
		UserEntity user = new UserEntity();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setCreatedOn(new Date(System.currentTimeMillis()));
		user = userRepository.save(user);
		
		String vetificationLink = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() 
				+ "/verify?token=" + verificationService.generateToken(user, VerificationType.REGISTER);
		
		String subject = i18nMessages.get("email.subject.registered", user.getUsername());
		Context context = new Context();
		context.setVariable("name", user.getUsername());
		context.setVariable("subject", subject);
		context.setVariable("vetificationLink", vetificationLink);
		EmailTemplate template = new EmailTemplate();
		template.setSubject(subject);
		template.setFrom(mailFrom);
		template.setTo(user.getEmail());
		template.setProps(context);
		template.setTemplateName("email_verification");
		emailUtil.sendEmail(template);
	}
	
	@Override
	public LoginResponse loginUser(LoginRequest request) {
		UserDetails userDetails = loadUserByUsername(request.getUsername());
		if (Objects.isNull(userDetails)) {
			throw new BadRequestException(i18nMessages.get("rest.error.user-pass.invalid"));
		}
		
		authenticate(request.getUsername(), request.getPassword());
		
		String jwtToken = jwtUtils.generateJwt(userDetails);

		return new LoginResponse(jwtToken);
	}	
	
	@Override
	public void forgetPassword(String email) {
		Optional<UserEntity> userOptional = userRepository.findByEmail(email);
		userOptional.orElseThrow(() -> new BadRequestException(i18nMessages.get("rest.error.invalid.email")));
		
		UserEntity user = userOptional.get();
		
		String vetificationLink = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() 
				+ "/verify?token=" + verificationService.generateToken(user, VerificationType.FORGET_PASSWORD);
		
		String subject = i18nMessages.get("email.subject.forget-password", user.getUsername());
		Context context = new Context();
		context.setVariable("name", user.getUsername());
		context.setVariable("subject", subject);
		context.setVariable("vetificationLink", vetificationLink);
		EmailTemplate template = new EmailTemplate();
		template.setSubject(subject);
		template.setFrom(mailFrom);
		template.setTo(user.getEmail());
		template.setProps(context);
		template.setTemplateName("forget-password");
		emailUtil.sendEmail(template);
	}
	
	@Override
	public void resetPassword(ResetPasswordRequest request) {
		boolean isValidPassword = Pattern.matches(PASSWORD_VALIDATION_PATTERN, request.getPassword());
		if(!isValidPassword) {
			throw new BadRequestException(i18nMessages.get("rest.error.invalid.password"));
		}
		
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
		
		UserEntity user = userRepository.findByUsername(userDetails.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		userRepository.save(user);
	}
	
	private void authenticate(String username, String password) throws BadRequestException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new BadRequestException("User Is Disabled");
		} catch (BadCredentialsException e) {
			throw new BadRequestException("INVALID_CREDENTIALS", e);
		}
	}

}
