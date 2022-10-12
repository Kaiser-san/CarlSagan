package com.ldjuric.saga.user;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserMQConfig {
    private static final String QUEUE_NAME_USER_IN = "user_input";

    private static final String QUEUE_NAME_USER_OUT = "user_output";


    @Bean
    public Queue userInputQueue() {
        return new Queue(QUEUE_NAME_USER_IN, true);
    }

    @Bean
    public Queue userOutputQueue() {
        return new Queue(QUEUE_NAME_USER_OUT, true);
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