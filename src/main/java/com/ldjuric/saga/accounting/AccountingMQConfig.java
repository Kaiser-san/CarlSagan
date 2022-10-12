package com.ldjuric.saga.accounting;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountingMQConfig {
    @Bean
    public Queue accountingInputOrchestrationQueue() {
        return new Queue("accounting_input_orchestration", true);
    }

    @Bean
    public Queue accountingOutputChoreographyQueue() {
        return new Queue("accounting_output_choreography", true);
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
}
