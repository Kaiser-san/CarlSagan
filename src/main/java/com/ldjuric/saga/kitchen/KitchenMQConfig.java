package com.ldjuric.saga.kitchen;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KitchenMQConfig {
    private static final String QUEUE_NAME_KITCHEN_CREATE_APPOINTMENT = "kitchen_create_appointment";
    private static final String QUEUE_NAME_KITCHEN_VALIDATE_APPOINTMENT = "kitchen_validate_appointment";
    private static final String QUEUE_NAME_KITCHEN_OUT = "kitchen_output";

    @Bean
    public Queue kitchenCreateQueue() {
        return new Queue(QUEUE_NAME_KITCHEN_CREATE_APPOINTMENT, true);
    }

    @Bean
    public Queue kitchenValidateQueue() {
        return new Queue(QUEUE_NAME_KITCHEN_VALIDATE_APPOINTMENT, true);
    }

    @Bean
    public Queue kitchenOutputQueue() {
        return new Queue(QUEUE_NAME_KITCHEN_OUT, true);
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
