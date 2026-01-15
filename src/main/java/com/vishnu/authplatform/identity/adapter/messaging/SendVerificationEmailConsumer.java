package com.vishnu.authplatform.identity.adapter.messaging;

import com.vishnu.authplatform.identity.adapter.email.EmailSender;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.vishnu.authplatform.identity.adapter.messaging.AmqpIdentityConfig.QUEUE;

@Component
public class SendVerificationEmailConsumer {

    private final EmailSender emailSender;
    private final String verificationBaseUrl;

    public SendVerificationEmailConsumer(EmailSender emailSender,
                                         @Value("${app.verification.base-url:http://localhost:8080}") String verificationBaseUrl) {
        this.emailSender = emailSender;
        this.verificationBaseUrl = verificationBaseUrl;
    }

    @RabbitListener(queues = QUEUE)
    public void handle(SendVerificationEmailMessage msg) {
        String link = verificationBaseUrl + "/api/v1/users/verify-email?token=" + msg.rawToken();
        emailSender.sendVerificationEmail(msg.email(), link);
    }
}
