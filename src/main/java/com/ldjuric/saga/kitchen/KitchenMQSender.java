package com.ldjuric.saga.kitchen;

import org.json.JSONObject;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class KitchenMQSender {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private Queue kitchenOutputQueue;

    public void sendSucess(Integer orderID, String name, Integer cost) {
    }

    public void sendFailure(Integer orderID) {

    }
}
