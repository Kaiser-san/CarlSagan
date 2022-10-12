package com.ldjuric.saga.accounting;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountingMQSender {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue accountingOutputQueue;

    public void sendSuccess(Integer orderID) {
    }

    public void sendFailure(Integer orderID) {

    }
}
