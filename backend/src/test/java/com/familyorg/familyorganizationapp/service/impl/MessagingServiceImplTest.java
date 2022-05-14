package com.familyorg.familyorganizationapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.service.MessagingService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;

public class MessagingServiceImplTest {

  private Environment env;
  private JavaMailSender mailSender;
  private TaskExecutor taskExecutor;
  private MessagingService messagingService;

  @BeforeEach
  public void setup() {
    env = mock(Environment.class);
    mailSender = mock(JavaMailSender.class);
    taskExecutor = mock(TaskExecutor.class);
    when(env.getProperty("server.domain")).thenReturn("testdomain.com");
    when(env.getProperty("messaging.email")).thenReturn("test@testomain.com");
    messagingService = new MessagingServiceImpl(env, mailSender, taskExecutor);
  }

  @Test
  public void sendHtmlEmail_success() {
    /* Given */
    doNothing().when(taskExecutor).execute(any());
    String recipient = "test@test.com";
    String subject = "test";
    String content = "<p>Test</p>";

    /* When */
    assertDoesNotThrow(
        () -> {
          messagingService.sendHtmlEmail(recipient, subject, content);
        });

    /* Then */
    // nothing, as long as an error isn't thrown the test passes
  }

  @Test
  public void sendPlainTestEmail_success() {
    /* Given */
    doNothing().when(taskExecutor).execute(any());
    String recipient = "test@test.com";
    String subject = "test";
    String content = "Test";

    /* When */
    assertDoesNotThrow(
        () -> {
          messagingService.sendPlainTextEmail(recipient, subject, content);
        });

    /* Then */
    // nothing, as long as an error isn't thrown the test passes
  }
}
