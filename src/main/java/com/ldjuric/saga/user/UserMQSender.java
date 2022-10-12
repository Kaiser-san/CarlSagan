package com.ldjuric.saga.user;

import org.json.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class UserMQSender {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue userOutputQueue;

    public void send(boolean result, int orderID, String username) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", result);
        jsonMessage.put("order_id", orderID);
        jsonMessage.put("username", username);
        String message = jsonMessage.toString();
        this.template.convertAndSend(userOutputQueue.getName(), message);
        System.out.println(" [user service] Sent '" + message + "'");
    }
}
