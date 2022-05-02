package com.familyorg.familyorganizationapp.configuration;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SpaRedirectFilterConfiguration {

	@Bean
	public FilterRegistrationBean spaRedirectFilter() {
		FilterRegistrationBean<OncePerRequestFilter> registration =
				new FilterRegistrationBean<>();
		registration.setFilter(createRedirectFilter());
		registration.addUrlPatterns("/*");
		registration.setName("frontendRedirect");
		registration.setOrder(1);
		return registration;
	}

	private OncePerRequestFilter createRedirectFilter() {
		return new OncePerRequestFilter() {
			private final String REGEX = "(?!/actuator|/api|/_nuxt|/static|/index\\\\.html|/200\\\\.html|/favicon\\.ico|/img|/sw\\\\.js).*$";
			private Pattern pattern = Pattern.compile(REGEX);
			@Override
			protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
					 throws ServletException, IOException {
				if (pattern.matcher(req.getRequestURI()).matches() && !req.getRequestURI().equals("/")) {
					RequestDispatcher rd = req.getRequestDispatcher("/");
					rd.forward(req, res);
				} else {
					chain.doFilter(req, res);
				}
			}
		};
	}
}
