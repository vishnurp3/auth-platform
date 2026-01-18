package com.vishnu.authplatform.bdd;

import com.vishnu.authplatform.identity.adapter.email.EmailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Profile("test")
public class TestEmailSender implements EmailSender {

    private final CopyOnWriteArrayList<SentEmail> emails = new CopyOnWriteArrayList<>();

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        emails.add(new SentEmail(toEmail, verificationLink));
    }

    public int count() {
        return emails.size();
    }

    public SentEmail lastEmail() {
        if (emails.isEmpty()) {
            return null;
        }
        return emails.getLast();
    }

    public List<SentEmail> all() {
        return List.copyOf(emails);
    }

    public void clear() {
        emails.clear();
    }

    public record SentEmail(String to, String link) {
    }
}
