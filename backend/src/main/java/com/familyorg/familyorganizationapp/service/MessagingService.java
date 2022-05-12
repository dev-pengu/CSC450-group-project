package com.familyorg.familyorganizationapp.service;

public interface MessagingService {
  void sendHtmlEmail(String recipient, String subject, String content);

  void sendPlainTextEmail(String recipient, String subject, String content);

  String buildInviteContent(String inviteCode, String owner);

  String buildPasswordResetContent(String resetCode);
}
