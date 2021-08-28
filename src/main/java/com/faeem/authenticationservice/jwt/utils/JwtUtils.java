package com.faeem.authenticationservice.jwt.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.faeem.authenticationservice.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils implements Serializable {

	private static final long serialVersionUID = -8523733591476115285L;
	
    private static final int JWT_TOKEN_EXPIRATION_PERIOD = 5 * 60 * 60 * 1000;
    
    private static final int RESET_PASSWORD_TOKEN_EXPIRATION_PERIOD = 5 * 60;

    @Value("${jsonwebtoken.secretKey}")
    private String jwtSecret;

    @Value("${jsonwebtoken.issuer}")
    private String issuer;
    
    private String generateToken(Map<String, Object> claims, String subject, Boolean isResetToken) {
        Date currentDate = new Date(System.currentTimeMillis());
        
        return Jwts.builder()
            .setClaims(claims).setSubject(subject).setIssuedAt(currentDate)
            .setExpiration(DateUtils.addSeconds(currentDate, isResetToken ? RESET_PASSWORD_TOKEN_EXPIRATION_PERIOD : JWT_TOKEN_EXPIRATION_PERIOD))
            .setIssuer(issuer)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public <T> T getClaimFromToken(String jwtToken, Function<Claims, T> claimsResolver) {
        Claims claims = getClaimsFromJwt(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims getClaimsFromJwt(String jwtToken) {
        return Jwts.parser().setSigningKey(jwtSecret)
            .parseClaimsJws(jwtToken).getBody();
    }

    public Date getExpirationOfJwt(String jwtToken) {
        return getClaimFromToken(jwtToken, Claims::getExpiration);
    }

    public String getUsernameFromToken(String jwtToken) {
        return getClaimFromToken(jwtToken, Claims::getSubject);
    }

    private Boolean isExpired(String jwtToken) {
        return getExpirationOfJwt(jwtToken).before(new Date());
    }

    public String generateJwt(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails.getUsername(), Boolean.FALSE);
    }
    
    public String generateTokenForPasswordReset(UserEntity user) {
        return generateToken(new HashMap<>(), user.getUsername(), Boolean.TRUE);
    }

    public Boolean validateJwt(String jwtToken, UserDetails userDetails) {
        return StringUtils.equals(getUsernameFromToken(jwtToken), userDetails.getUsername()) 
            && BooleanUtils.isFalse(isExpired(jwtToken));
    }

}
