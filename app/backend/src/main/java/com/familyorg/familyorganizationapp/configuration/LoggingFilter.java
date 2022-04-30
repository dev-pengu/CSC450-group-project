package com.familyorg.familyorganizationapp.configuration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

    long startTime = System.currentTimeMillis();
    filterChain.doFilter(requestWrapper, responseWrapper);
    long timeTaken = System.currentTimeMillis() - startTime;

    String requestBody =
        getStringValue(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());

    logger.info(
        "FINISHED PROCESSING : TIME TAKEN={}; METHOD={}; REQUESTURI={}; REQUEST PAYLOAD={}; RESPONSE CODE={}; {}; {}; {}",
        timeTaken, request.getMethod(), request.getRequestURI(),
        request.getRequestURI().contains("auth") ? "***" : requestBody, response.getStatus(), request.getRemoteAddr(), request.getRemoteHost(), request.getRemotePort());
    responseWrapper.copyBodyToResponse();
  }

  private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
    try {
      return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
    } catch (UnsupportedEncodingException e) {
      logger.error(e.getMessage(), e);
    }
    return "";
  }
}
