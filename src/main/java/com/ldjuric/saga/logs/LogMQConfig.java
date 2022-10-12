package com.ldjuric.saga.logs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogMQConfig {

    @Bean
    public Queue logInputQueue() {
        return new Queue("log_input", true);
    }

    @Bean
    public LogMQReceiver logReceiver() {
        return new LogMQReceiver();
    }
}
