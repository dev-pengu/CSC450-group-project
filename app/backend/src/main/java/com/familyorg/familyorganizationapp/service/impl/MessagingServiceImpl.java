package com.familyorg.familyorganizationapp.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.familyorg.familyorganizationapp.service.MessagingService;

@Service
public class MessagingServiceImpl implements MessagingService {

  private static Logger logger = LoggerFactory.getLogger(MessagingServiceImpl.class);

  private Environment env;
  private String domain;

  private JavaMailSender mailSender;
  private TaskExecutor taskExecutor;


  @Autowired
  public MessagingServiceImpl(
      Environment env, JavaMailSender mailSender, TaskExecutor taskExecutor) {
    this.env = env;
    domain =
        env.getProperty("server.domain") != null
            ? env.getProperty("server.domain")
            : (env.getProperty("server.host") + ":" + env.getProperty("server.port"));
    this.mailSender = mailSender;
    this.taskExecutor = taskExecutor;
  }

  private static String inviteTemplateContents;
  private static String passwordResetTemplateContents;

  private static String getInviteTemplateContents() {
    if (inviteTemplateContents != null) {
      return inviteTemplateContents;
    }
    try {
      final byte[] bytes = StreamUtils.copyToByteArray(
        new ClassPathResource("/email_templates/invite-template/index.html").getInputStream());
      inviteTemplateContents = new String(bytes, StandardCharsets.UTF_8);
      return inviteTemplateContents;
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      return null;
    }
  }

  private static String getPasswordResetTemplateContents() {
    if (passwordResetTemplateContents != null) {
      return passwordResetTemplateContents;
    }
    try {
      final byte[] bytes = StreamUtils.copyToByteArray(
        new ClassPathResource("/email_templates/password-reset-template/index.html").getInputStream());
        passwordResetTemplateContents = new String(bytes, StandardCharsets.UTF_8);
      return passwordResetTemplateContents;
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      return null;
    }
  }

  public void sendHtmlEmail(String recipient, String subject, String content) {
    Objects.requireNonNull(recipient);
    Objects.requireNonNull(subject);
    Objects.requireNonNull(content);
    try {
      sendMail(content, env.getProperty("messaging.email"), recipient, subject, true);
    } catch (Exception e) {
      logger.error("Failed to send email. Reason: " + e.getMessage());
    }
  }

  public void sendPlainTextEmail(String recipient, String subject, String content) {
    Objects.requireNonNull(recipient);
    Objects.requireNonNull(subject);
    Objects.requireNonNull(content);
    try {
      sendMail(content, env.getProperty("messaging.email"), recipient, subject, false);
    } catch (Exception e) {
      logger.error("Failed to send email. Reason: " + e.getMessage());
    }
  }

  public void sendMail(
      final String text,
      final String from,
      final String to,
      final String subject,
      final boolean isHtml)
      throws Exception {
    if (Boolean.parseBoolean(env.getProperty("messaging.use.smtp", "false"))) {
      taskExecutor.execute(
          new Runnable() {
            @Override
            public void run() {
              try {
                sendMailSimple(text, from, to, subject, isHtml);
              } catch (Exception e) {
                logger.error("Failed to send email to: " + to + " reason: " + e.getMessage());
              }
            }
          });
      }
  }

  private void sendMailSimple(String text, String from, String to, String subject, boolean isHtml)
      throws Exception {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(text, isHtml);
    } catch (MessagingException e) {
      throw new MailParseException(e);
    }
    mailSender.send(message);

    if (logger.isDebugEnabled()) {
      logger.debug("Mail was sent successfully to: " + to);
    }
  }

  @Override
  public String buildInviteContent(String inviteCode, String owner) {
    String joinLink = "https://" + domain + "/login?code=" + inviteCode;
    String contents = getInviteTemplateContents();
    if (contents == null) {
      return null;
    }
    contents = contents.replace("|FAMILY-CODE|", inviteCode);
    contents = contents.replace("|JOIN-LINK|", joinLink);
    contents = contents.replace("|OWNER-NAME|", owner);
    contents = contents.replace("|LOGO-LINK|", "https://" + domain + "/img/logo-light.png");


    return contents;
  }

  @Override
  public String buildPasswordResetContent(String resetCode) {
    String resetLink = "https://" + domain + "/passwordReset?code=" + resetCode;
    String contents = getPasswordResetTemplateContents();
    if (contents == null) {
      return null;
    }
    contents = contents.replace("|RESET-LINK|", resetLink);
    contents = contents.replace("|LOGO-LINK|", "https://" + domain + "/img/logo-light.png");


    return contents;
  }
}
