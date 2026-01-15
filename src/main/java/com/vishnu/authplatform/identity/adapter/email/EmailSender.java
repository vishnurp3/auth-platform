package com.vishnu.authplatform.identity.adapter.email;

public interface EmailSender {
    void sendVerificationEmail(String toEmail, String verificationLink);
}
