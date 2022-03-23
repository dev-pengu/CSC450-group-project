package com.familyorg.familyorganizationapp.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface MessagingService {
  void sendHtmlEmail(String recipient, String subject, String content)
      throws AddressException, MessagingException;

  void sendPlainTextEmail(String recipient, String subject, String content)
      throws AddressException, MessagingException;

  String buildInviteContent(String inviteCode, String owner);
}
