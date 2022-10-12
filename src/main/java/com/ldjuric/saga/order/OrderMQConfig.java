package com.ldjuric.saga.order;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMQConfig {
    @Bean
    public Queue orderOutputQueue() {
        return new Queue("order_ouput", true);
    }

    @Bean
    public OrderMQReceiver orderReceiver() {
        return new OrderMQReceiver();
    }

    @Bean
    public OrderMQSender orderSender() {
        return new OrderMQSender();
    }

    @Bean
    public OrderCreateOrchestrator createOrchestrator() {
        return new OrderCreateOrchestrator();
    }
}
