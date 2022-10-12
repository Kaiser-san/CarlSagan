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

    public void sendSuccess(Integer orderID, Integer kitchenAppointmentID, Integer cost) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", true);
        jsonMessage.put("order_id", orderID);
        jsonMessage.put("kitchen_appointment_id", kitchenAppointmentID);
        jsonMessage.put("cost", cost);
        String message = jsonMessage.toString();
        this.template.convertAndSend(kitchenOutputQueue.getName(), message);
        System.out.println(" [kitchen service] Sent '" + message + "'");
    }

    public void sendFailure(Integer orderID) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("validated", false);
        jsonMessage.put("order_id", orderID);
        String message = jsonMessage.toString();
        this.template.convertAndSend(kitchenOutputQueue.getName(), message);
        System.out.println(" [kitchen service] Sent '" + message + "'");
    }
}
