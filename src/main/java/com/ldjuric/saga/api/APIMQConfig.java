package com.ldjuric.saga.api;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class APIMQConfig {

    private static final String QUEUE_NAME = "hello";


    @Bean
    public Queue createQueue() {
        //For learning purpose - durable=false,
        // in a real project you may need to set this as true.
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public APIMQReceiver receiver() {
        return new APIMQReceiver();
    }

    @Bean
    public APIMQSender sender() {
        return new APIMQSender();
    }
}
