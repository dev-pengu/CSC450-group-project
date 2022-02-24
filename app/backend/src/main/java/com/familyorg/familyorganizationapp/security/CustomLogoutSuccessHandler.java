package com.familyorg.familyorganizationapp.security;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler, Serializable {

	private static final long serialVersionUID = -1862221542887044893L;
	private Logger LOG = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		try {
			SecurityContextHolder.getContext().setAuthentication(null);
			SecurityContextHolder.clearContext();
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			response.addHeader("Content-Type", "applicatin/json");
			response.getWriter().write("success");
		} catch (Exception e) {
			try {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.addHeader("Content-Type", "applicatin/json");
				response.getWriter().write("failed");
			} catch (IOException ioe) {
				LOG.error("Error", ioe);
			}
		}
	}
}
