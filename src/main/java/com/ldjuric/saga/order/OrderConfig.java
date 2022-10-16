package com.ldjuric.saga.order;

import com.ldjuric.saga.accounting.AccountingService;
import com.ldjuric.saga.logs.LogService;
import com.ldjuric.saga.user.UserService;
import com.ldjuric.saga.warehouse.WarehouseService;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class OrderConfig {
    @Bean
    public FanoutExchange orderFanout() {
        return new FanoutExchange("order.fanout");
    }

    @Profile({"order", "all"})
    @Bean
    public OrderMessageReceiver orderReceiver() {
        return new OrderMessageReceiver();
    }

    @Profile({"order", "all"})
    @Bean
    public OrderMessageSender orderSender() {
        return new OrderMessageSender();
    }

    @Profile({"order", "all"})
    @Bean
    public OrderCreateOrchestrator createOrchestrator(OrderMessageSender orderSender) {
        return new OrderCreateOrchestrator(orderSender, orderSender, orderSender, orderSender);
    }
//    Uncomment these lines and delete above to make this stuff work as a monolith with the all profile
//    @Profile("all")
//    @Bean
//    public OrderCreateOrchestrator createOrchestrator(LogService logService, AccountingService accountingService, UserService userService, WarehouseService warehouseService) {
//        return new OrderCreateOrchestrator(logService, accountingService, userService, warehouseService);
//    }
//
//    @Profile("order")
//    @Bean
//    public OrderCreateOrchestrator createOrchestrator(OrderMessageSender orderSender) {
//        return new OrderCreateOrchestrator(orderSender, orderSender, orderSender, orderSender);
//    }

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
