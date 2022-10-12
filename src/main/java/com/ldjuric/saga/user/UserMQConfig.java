package com.ldjuric.saga.user;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserMQConfig {

    @Bean
    public Queue userInputQueue() {
        return new Queue("user_input", true);
    }

    @Bean
    public Queue userOutputQueue() {
        return new Queue("user_output", true);
    }

    @Bean
    public UserMQReceiver userReceiver() {
        return new UserMQReceiver();
    }

    @Bean
    public UserMQSender userSender() {
        return new UserMQSender();
    }
}