package com.ldjuric.saga.api;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class APIMQSender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue createQueue;

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        String message = "Hello World!";
        this.template.convertAndSend(createQueue.getName(), message);
        System.out.println(" [x] Sent '" + message + "'");
    }
}
