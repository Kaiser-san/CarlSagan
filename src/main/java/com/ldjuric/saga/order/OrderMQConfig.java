package com.ldjuric.saga.order;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderMQConfig {
    @Bean
    public FanoutExchange orderFanout() {
        return new FanoutExchange("order.fanout");
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
