package com.ldjuric.saga.warehouse;

import org.json.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseMQSender {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue warehouseOutputOrchestrationQueue;

    @Autowired
    private Queue warehouseOutputChoreographyQueue;

    public void sendSuccessOrchestration(int orderID, Integer warehouseReservationID, Integer cost) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("warehouseReservationID", warehouseReservationID);
        jsonMessage.put("cost", cost);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputOrchestrationQueue.getName(), message);
        System.out.println(" [warehouse service] Sent '" + message + "'");
    }

    public void sendFailureOrchestration(int orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("orderID", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputOrchestrationQueue.getName(), message);
        System.out.println(" [warehouse service] Sent '" + message + "'");
    }

    public void sendSuccessChoreography(int orderID, Integer warehouseReservationID, Integer cost) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("warehouseReservationID", warehouseReservationID);
        jsonMessage.put("cost", cost);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputChoreographyQueue.getName(), message);
        System.out.println(" [warehouse service] Sent '" + message + "'");
    }

    public void sendFailureChoreography(int orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("orderID", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputChoreographyQueue.getName(), message);
        System.out.println(" [warehouse service] Sent '" + message + "'");
    }
}
