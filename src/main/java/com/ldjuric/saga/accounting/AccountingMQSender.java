package com.ldjuric.saga.accounting;

import org.json.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountingMQSender {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue accountingOutputQueue;

    public void sendSuccess(Integer orderID, Integer accountingTransactionID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("accountingTransactionID", accountingTransactionID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputQueue.getName(), message);
        System.out.println(" [accounting service] Sent '" + message + "'");
    }

    public void sendFailure(Integer orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("orderID", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputQueue.getName(), message);
        System.out.println(" [accounting service] Sent '" + message + "'");
    }

}
