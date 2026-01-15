package com.vishnu.authplatform.identity.adapter.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpIdentityConfig {

    public static final String EXCHANGE = "auth.identity.exchange";
    public static final String QUEUE = "auth.identity.sendVerificationEmail.queue";
    public static final String ROUTING_KEY = "identity.sendVerificationEmail";

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DirectExchange identityExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue sendVerificationEmailQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    Binding bindSendVerificationEmail(Queue sendVerificationEmailQueue, DirectExchange identityExchange) {
        return BindingBuilder.bind(sendVerificationEmailQueue).to(identityExchange).with(ROUTING_KEY);
    }
}
