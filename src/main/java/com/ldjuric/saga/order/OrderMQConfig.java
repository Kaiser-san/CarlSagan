package com.ldjuric.saga.order;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class OrderMQConfig {
    @Bean
    public FanoutExchange orderFanout() {
        return new FanoutExchange("order.fanout");
    }

    @Profile({"order", "all"})
    @Bean
    public OrderMQReceiver orderReceiver() {
        return new OrderMQReceiver();
    }

    @Profile({"order", "all"})
    @Bean
    public OrderMQSender orderSender() {
        return new OrderMQSender();
    }

    @Profile({"order", "all"})
    @Bean
    public OrderCreateOrchestrator createOrchestrator() {
        return new OrderCreateOrchestrator();
    }

    @Bean
    public Queue orderAccountingOutputQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding orderAccountingOutputBinding(FanoutExchange accountingFanout,
                                                    Queue orderAccountingOutputQueue) {
        return BindingBuilder.bind(orderAccountingOutputQueue).to(accountingFanout);
    }
}
