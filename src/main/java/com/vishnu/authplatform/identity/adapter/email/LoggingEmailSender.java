package com.vishnu.authplatform.identity.adapter.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!test")
public class LoggingEmailSender implements EmailSender {

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        log.info("Verification email to={} link={}", toEmail, verificationLink);
    }
}
