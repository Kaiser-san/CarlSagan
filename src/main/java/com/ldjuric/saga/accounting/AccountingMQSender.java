package com.ldjuric.saga.accounting;

import com.ldjuric.saga.interfaces.LogServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class AccountingMQSender implements LogServiceInterface {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue accountingOutputOrchestrationQueue;

    @Autowired
    private FanoutExchange accountingFanout;

    @Autowired
    private Queue logInputQueue;

    public void sendSuccessChoreography(Integer orderID, AccountingTransactionEntity accountingTransaction) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("accountingTransactionID", accountingTransaction.getId());
        jsonMessage.put("cost", accountingTransaction.getCost());
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingFanout.getName(),"", message);
        this.log("[AccountingService::sendSuccessChoreography] sent " + message);
    }

    public void sendFailureChoreography(Integer orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("orderID", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingFanout.getName(), "", message);
        this.log("[AccountingService::sendFailureChoreography] sent " + message);
    }

    public void sendSuccessOrchestration(int orderID, AccountingTransactionEntity accountingTransaction) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("accountingTransactionID", accountingTransaction.getId());
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputOrchestrationQueue.getName(), message);
        this.log("[AccountingService::sendSuccessOrchestration] sent " + message);
    }

    public void sendFailureOrchestration(int orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("orderID", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputOrchestrationQueue.getName(), message);
        this.log("[AccountingService::sendFailureOrchestration] sent " + message);
    }

    @Override
    public void log(String message) {
        this.template.convertAndSend(logInputQueue.getName(), message);
    }
}
