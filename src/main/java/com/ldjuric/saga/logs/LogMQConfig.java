package com.ldjuric.saga.logs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class LogMQConfig {

    @Bean
    public Queue logInputQueue() {
        return new Queue("log_input", true);
    }

    @Profile({"log", "all"})
    @Bean
    public LogMQReceiver logReceiver() {
        return new LogMQReceiver();
    }
}
