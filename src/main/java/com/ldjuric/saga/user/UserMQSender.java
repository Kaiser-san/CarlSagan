package com.ldjuric.saga.user;

import com.ldjuric.saga.interfaces.LogServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class UserMQSender implements LogServiceInterface {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue userOutputOrchestrationQueue;

    @Autowired
    private Queue userOutputChoreographyQueue;

    @Autowired
    private Queue logInputQueue;

    public void sendOrchestration(boolean result, int orderID, String username) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", result);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("username", username);
        String message = jsonMessage.toString();
        this.template.convertAndSend(userOutputOrchestrationQueue.getName(), message);
        this.log("[UserService::sendOrchestration] sent " + message);
    }

    public void sendChoreography(boolean result, int orderID, String username) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", result);
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("username", username);
        String message = jsonMessage.toString();
        this.template.convertAndSend(userOutputChoreographyQueue.getName(), message);
        this.log("[UserService::sendChoreography] sent " + message);
    }

    @Override
    public void log(String message) {
        this.template.convertAndSend(logInputQueue.getName(), message);
    }
}
