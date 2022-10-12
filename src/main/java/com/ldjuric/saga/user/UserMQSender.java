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

    public void send(boolean result) {
        String message = new JSONObject().put("result", result).toString();
        this.template.convertAndSend(userOutputQueue.getName(), message);
        System.out.println(" [user service] Sent '" + message + "'");
    }
}
