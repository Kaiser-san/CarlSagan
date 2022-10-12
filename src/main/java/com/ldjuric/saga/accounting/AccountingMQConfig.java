package com.ldjuric.saga.accounting;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class AccountingMQConfig {
    @Bean
    public Queue accountingCreateQueue() {
        return new Queue("accounting_create", true);
    }

    @Bean
    public Queue accountingCreateOrchestrationQueue() {
        return new Queue("accounting_create_orchestration", true);
    }

    @Bean
    public Queue accountingOutputQueue() {
        return new Queue("accounting_output", true);
    }

    @Bean
    public AccountingMQReceiver kitchenReceiver() {
        return new AccountingMQReceiver();
    }

    @Bean
    public AccountingMQSender kitchenSender() {
        return new AccountingMQSender();
    }
}
