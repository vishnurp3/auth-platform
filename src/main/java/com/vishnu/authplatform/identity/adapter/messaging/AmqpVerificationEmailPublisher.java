package com.vishnu.authplatform.identity.adapter.messaging;

import com.vishnu.authplatform.identity.application.port.VerificationEmailPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.vishnu.authplatform.identity.adapter.messaging.AmqpIdentityConfig.EXCHANGE;
import static com.vishnu.authplatform.identity.adapter.messaging.AmqpIdentityConfig.ROUTING_KEY;

@Component
@RequiredArgsConstructor
public class AmqpVerificationEmailPublisher implements VerificationEmailPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishSendVerificationEmail(UUID userId, String email, String rawToken) {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, new SendVerificationEmailMessage(userId, email, rawToken));
    }
}
