package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.interfaces.LogServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

@Profile({"warehouse", "all"})
public class WarehouseMQSender implements LogServiceInterface {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue warehouseOutputOrchestrationQueue;

    @Autowired
    private Queue warehouseOutputChoreographyQueue;

    @Autowired
    private Queue logInputQueue;

    public void sendSuccessOrchestration(int orderID, Integer cost) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("cost", cost);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputOrchestrationQueue.getName(), message);
        this.log("[WarehouseService::sendSuccessOrchestration] sent " + message);
    }

    public void sendFailureOrchestration(int orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("orderID", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputOrchestrationQueue.getName(), message);
        this.log("[WarehouseService::sendFailureOrchestration] sent " + message);
    }

    public void sendSuccessChoreography(int orderID, Integer cost) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("cost", cost);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputChoreographyQueue.getName(), message);
        this.log("[WarehouseService::sendSuccessChoreography] sent " + message);
    }

    public void sendFailureChoreography(int orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("orderID", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseOutputChoreographyQueue.getName(), message);
        this.log("[WarehouseService::sendFailureChoreography] sent " + message);
    }

    @Override
    public void log(String message) {
        this.template.convertAndSend(logInputQueue.getName(), message);
    }
}
