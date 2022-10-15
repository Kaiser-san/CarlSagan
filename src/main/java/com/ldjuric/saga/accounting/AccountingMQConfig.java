package com.ldjuric.saga.accounting;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountingMQConfig {

    @Bean
    public FanoutExchange accountingFanout() {
        return new FanoutExchange("accounting.fanout");
    }

    @Bean
    public Queue accountingInputOrchestrationQueue() {
        return new Queue("accounting_input_orchestration", true);
    }

    @Bean
    public Queue accountingOutputOrchestrationQueue() {
        return new Queue("accounting_output_orchestration", true);
    }

    @Bean
    public AccountingMQReceiver accountingReceiver() {
        return new AccountingMQReceiver();
    }

    @Bean
    public AccountingMQSender accountingSender() {
        return new AccountingMQSender();
    }

    @Bean
    public Queue accountingOrderOutputQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding accountingOrderOutputBinding(FanoutExchange orderFanout,
                                               Queue accountingOrderOutputQueue) {
        return BindingBuilder.bind(accountingOrderOutputQueue).to(orderFanout);
    }
}
