package com.ldjuric.saga.accounting;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AccountingConfig {

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

    @Profile({"accounting", "all"})
    @Bean
    public AccountingMessageReceiver accountingReceiver() {
        return new AccountingMessageReceiver();
    }

    @Profile({"accounting", "all"})
    @Bean
    public AccountingMessageSender accountingSender() {
        return new AccountingMessageSender();
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
