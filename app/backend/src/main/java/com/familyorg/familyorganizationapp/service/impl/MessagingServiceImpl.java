package com.familyorg.familyorganizationapp.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.service.MessagingService;

@Service
public class MessagingServiceImpl implements MessagingService {

  private static Logger LOG = LoggerFactory.getLogger(MessagingServiceImpl.class);

  @Autowired
  private Environment env;

  private static String inviteTemplateContents;

  private static String getInviteTemplateContents() {
    if (inviteTemplateContents != null) {
      return inviteTemplateContents;
    }
    try {
      FileReader fileReader =
          new FileReader("src/main/resources/email_templates/invite-template/index.html");
      try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
        StringBuilder content = new StringBuilder(19321);
        String s;
        while ((s = bufferedReader.readLine()) != null) {
          content.append(s);
        }
        inviteTemplateContents = content.toString();
      }
      return inviteTemplateContents;
    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage(), e);
      return null;
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  public void sendHtmlEmail(String recipient, String subject, String content) {

    Objects.requireNonNull(recipient);
    Objects.requireNonNull(subject);
    Objects.requireNonNull(content);

    try {
      Message message = new MimeMessage(getSession());
      message.setFrom(new InternetAddress(env.getProperty("messaging.email")));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
      message.setSubject(subject);
      message.setContent(content, "text/html");

      Transport.send(message);
    } catch (AddressException e) {
      throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
          "Recipient email is malformed.");
    } catch (MessagingException e) {
      throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
          "Error sending email to recipient");
    }
  }

  public void sendPlainTextEmail(String recipient, String subject, String content) {
    Objects.requireNonNull(recipient);
    Objects.requireNonNull(subject);
    Objects.requireNonNull(content);

    try {
      Message message = new MimeMessage(getSession());
      message.setFrom(new InternetAddress(env.getProperty("messaging.email")));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
      message.setSubject(subject);
      message.setText(content);

      Transport.send(message);
    } catch (AddressException e) {
      throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
          "Recipient email is malformed.");
    } catch (MessagingException e) {
      throw new BadRequestException(ApiExceptionCode.BAD_PARAM_VALUE,
          "Error sending email to recipient");
    }
  }

  public String buildInviteContent(String inviteCode, String owner) {
    // TODO: replace unsub link |UNSUB-LINK| in the template
    String joinLink = "http://" + env.getProperty("server.host") + ":"
        + env.getProperty("server.port") + "/family/join&code=" + inviteCode;
    String contents = getInviteTemplateContents();
    if (contents == null) {
      return null;
    }
    contents = contents.replace("|FAMILY-CODE|", inviteCode);
    contents = contents.replace("|JOIN-LINK|", joinLink);
    contents = contents.replace("|OWNER-NAME|", owner);

    return contents;
  }

  private Session getSession() {
    Properties props = new Properties();
    props.put("mail.smtp.host", env.getProperty("messaging.smtp.server"));
    props.put("mail.smtp.socketFactory.port", env.getProperty("messaging.smtp.port"));
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", env.getProperty("messaging.smtp.port"));
    Authenticator auth = new SMTPAuthenticator();
    return Session.getInstance(props, auth);
  }

  private class SMTPAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(env.getProperty("messaging.email"),
          env.getProperty("messaging.password"));
    }
  }
}
