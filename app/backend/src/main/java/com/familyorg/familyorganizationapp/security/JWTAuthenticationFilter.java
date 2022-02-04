package com.familyorg.familyorganizationapp.security;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.familyorg.familyorganizationapp.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	AuthenticationManager authenticationManager;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		
		setFilterProcessesUrl("/api/services/auth/login");
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
                                            	HttpServletResponse res) 
		throws AuthenticationException {
		try {
			User creds = new ObjectMapper()
					.readValue(req.getInputStream(), User.class);
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
						creds.getUsername(),
						creds.getPassword(),
						new ArrayList<>()
					));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication auth) throws IOException {
		Instant now = Instant.now();
		org.springframework.security.core.userdetails.User user = 
				(org.springframework.security.core.userdetails.User) auth.getPrincipal();
		String token = Jwts.builder()
				.claim("username", user.getUsername())
				.setSubject(user.getUsername().toLowerCase())
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(now.plus(SecurityConstants.EXPIRATION_TIME, ChronoUnit.MILLIS)))
				.signWith(new SecretKeySpec(
					Base64.getDecoder().decode(SecurityConstants.SECRET),
					SignatureAlgorithm.HS256.getJcaName()))
				.compact();
		String body = "{\"token\": \"" + token + "\",\"expires\":\"" + 
				Date.from(now.plus(SecurityConstants.EXPIRATION_TIME, ChronoUnit.MILLIS)).toString() + "\"}";
		
		res.getWriter().write(body);
		res.getWriter().flush();
	}
}
