package com.vishnu.authplatform.bdd;

import com.vishnu.authplatform.identity.adapter.email.EmailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class InMemoryEmailSender implements EmailSender {

    private final AtomicReference<SentEmail> last = new AtomicReference<>();

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        last.set(new SentEmail(toEmail, verificationLink));
    }

    public SentEmail lastEmail() {
        return last.get();
    }

    public record SentEmail(String to, String link) {
    }
}
