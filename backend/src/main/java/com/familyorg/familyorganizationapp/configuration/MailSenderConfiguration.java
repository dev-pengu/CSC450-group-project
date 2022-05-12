package com.familyorg.familyorganizationapp.configuration;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailSenderConfiguration {

  private Logger logger = LoggerFactory.getLogger(MailSenderConfiguration.class);
  @Autowired
  private Environment env;

  @Bean
  public JavaMailSender mailSender() {
    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    sender.setHost(env.getProperty("messaging.smtp.server"));
    sender.setPort(Integer.parseInt(env.getProperty("messaging.smtp.port")));
    sender.setProtocol("smtp");
    sender.setUsername(env.getProperty("messaging.email"));
    sender.setPassword(env.getProperty("messaging.password"));
    Properties mailProps = new Properties();
    mailProps.put("mail.smtp.auth", true);
    mailProps.put("mail.smtp.starttls.enable", true);
    mailProps.put("mail.smtp.starttls.required", true);
    mailProps.put("mail.smtp.socketFactory.port", Integer.parseInt(env.getProperty("messaging.smtp.port")));
    mailProps.put("mail.smtp.debug", true);
    mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    mailProps.put("mail.smtp.socketFactory.fallback", false);
    sender.setJavaMailProperties(mailProps);
    logger.info("JavaMailSender bean initialized: " + sender.toString());
    return sender;
  }
}
