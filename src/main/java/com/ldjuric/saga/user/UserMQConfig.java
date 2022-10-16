package com.ldjuric.saga.user;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class UserMQConfig {

    @Bean
    public Queue userInputQueue() {
        return new Queue("user_input_orchestration", true);
    }

    @Bean
    public Queue userOutputOrchestrationQueue() {
        return new Queue("user_output_orchestration", true);
    }

    @Bean
    public Queue userOutputChoreographyQueue() {
        return new Queue("user_output_choreography", true);
    }

    @Profile({"user", "all"})
    @Bean
    public UserMQReceiver userReceiver() {
        return new UserMQReceiver();
    }

    @Profile({"user", "all"})
    @Bean
    public UserMQSender userSender() {
        return new UserMQSender();
    }

    @Bean
    public Queue userOrderOutputQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding userOrderOutputBinding(FanoutExchange orderFanout,
                                          Queue userOrderOutputQueue) {
        return BindingBuilder.bind(userOrderOutputQueue).to(orderFanout);
    }
}