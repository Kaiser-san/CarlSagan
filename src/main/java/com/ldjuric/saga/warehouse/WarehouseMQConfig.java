package com.ldjuric.saga.warehouse;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WarehouseMQConfig {
    @Bean
    public Queue warehouseCreateQueue() {
        return new Queue("warehouse_input", true);
    }

    @Bean
    public Queue warehouseOutputOrchestrationQueue() {
        return new Queue("warehouse_output_orchestration", true);
    }

    @Bean
    public Queue warehouseOutputChoreographyQueue() {
        return new Queue("warehouse_output_choreography", true);
    }

    @Bean
    public WarehouseMQReceiver warehouseReceiver() {
        return new WarehouseMQReceiver();
    }

    @Bean
    public WarehouseMQSender warehouseSender() {
        return new WarehouseMQSender();
    }

    @Bean
    public Queue warehouseOrderOutputQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding warehouseOrderOutputBinding(FanoutExchange orderFanout,
                                          Queue warehouseOrderOutputQueue) {
        return BindingBuilder.bind(warehouseOrderOutputQueue).to(orderFanout);
    }

    @Bean
    public Queue warehouseAccountingOutputQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding warehouseAccountingOutputBinding(FanoutExchange accountingFanout,
                                               Queue warehouseAccountingOutputQueue) {
        return BindingBuilder.bind(warehouseAccountingOutputQueue).to(accountingFanout);
    }
}
