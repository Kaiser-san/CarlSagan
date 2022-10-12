package com.ldjuric.saga.accounting;

import org.json.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountingMQSender {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue accountingOutputChoreographyQueue;

    @Autowired
    private Queue accountingOutputOrchestrationQueue;

    public void sendSuccessChoreography(Integer orderID, AccountingTransactionEntity accountingTransaction) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("order_id", orderID);
        jsonMessage.put("accounting_transaction_id", accountingTransaction.getId());
        jsonMessage.put("kitchen_appointment_id", accountingTransaction.getKitchenAppointmentID());
        jsonMessage.put("cost", accountingTransaction.getCost());
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputChoreographyQueue.getName(), message);
        System.out.println(" [accounting service] Sent '" + message + "'");
    }

    public void sendFailureChoreography(Integer orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("order_id", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputChoreographyQueue.getName(), message);
        System.out.println(" [accounting service] Sent '" + message + "'");
    }

    public void sendSuccessOrchestration(int orderID, AccountingTransactionEntity accountingTransaction) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("order_id", orderID);
        jsonMessage.put("accounting_transaction_id", accountingTransaction.getId());
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputOrchestrationQueue.getName(), message);
        System.out.println(" [accounting service] Sent '" + message + "'");
    }

    public void sendFailureOrchestration(int orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("order_id", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingOutputOrchestrationQueue.getName(), message);
        System.out.println(" [accounting service] Sent '" + message + "'");
    }
}
