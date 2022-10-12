package com.ldjuric.saga.kitchen;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KitchenMQConfig {
    @Bean
    public Queue kitchenCreateQueue() {
        return new Queue("kitchen_input", true);
    }

    @Bean
    public Queue kitchenOutputQueue() {
        return new Queue("kitchen_output", true);
    }

    @Bean
    public KitchenMQReceiver kitchenReceiver() {
        return new KitchenMQReceiver();
    }

    @Bean
    public KitchenMQSender kitchenSender() {
        return new KitchenMQSender();
    }
}
