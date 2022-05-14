package com.familyorg.familyorganizationapp.integration;

import com.familyorg.familyorganizationapp.service.MessagingService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MessagingServiceImplTestContextConfiguration {

  @Bean
  public MessagingService messagingServiceImpl() {
    return new MessagingService() {
      @Override
      public void sendHtmlEmail(String recipient, String subject, String content) {

      }

      @Override
      public void sendPlainTextEmail(String recipient, String subject, String content) {

      }

      @Override
      public String buildInviteContent(String inviteCode, String owner) {
        return null;
      }

      @Override
      public String buildPasswordResetContent(String resetCode) {
        return null;
      }
    };
  }
}
