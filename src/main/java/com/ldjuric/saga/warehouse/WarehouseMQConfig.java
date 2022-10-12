package com.ldjuric.saga.warehouse;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WarehouseMQConfig {
    @Bean
    public Queue warehouseCreateQueue() {
        return new Queue("warehouse_input", true);
    }

    @Bean
    public Queue warehouseOutputQueue() {
        return new Queue("warehouse_output", true);
    }

    @Bean
    public WarehouseMQReceiver warehouseReceiver() {
        return new WarehouseMQReceiver();
    }

    @Bean
    public WarehouseMQSender warehouseSender() {
        return new WarehouseMQSender();
    }
}
