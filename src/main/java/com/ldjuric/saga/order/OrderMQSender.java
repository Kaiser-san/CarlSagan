package com.ldjuric.saga.order;

import com.ldjuric.saga.interfaces.LogServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderMQSender implements LogServiceInterface {
    @Autowired
    private RabbitTemplate template;

    @Autowired
    private FanoutExchange orderFanout;

    @Autowired
    private Queue userInputQueue;

    @Autowired
    private Queue warehouseInputQueue;

    @Autowired
    private Queue accountingInputOrchestrationQueue;

    @Autowired
    private Queue logInputQueue;

    public void sendChoreography(Integer orderID, Integer orderType, String username, String password) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("orderType", orderType);
        jsonMessage.put("username", username);
        jsonMessage.put("password", password);
        String message = jsonMessage.toString();
        this.template.convertAndSend(orderFanout.getName(), "", message);
        this.log("[OrderService::sendChoreography] sent " + message);
    }

    public void sendUserValidate(Integer orderID, String username, String password) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("username", username);
        jsonMessage.put("password", password);
        String message = jsonMessage.toString();
        this.template.convertAndSend(userInputQueue.getName(), message);
        this.log("[OrderService::sendUserValidate] sent " + message);
    }

    public void sendKitchenValidate(Integer orderID, Integer orderType) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("orderID", orderID);
        jsonMessage.put("orderType", orderType);
        String message = jsonMessage.toString();
        this.template.convertAndSend(warehouseInputQueue.getName(), message);
        this.log("[OrderService::sendKitchenValidate] sent " + message);
    }

    public void sendAccountingValidate(OrderEntity orderEntity) {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("orderID", orderEntity.getId());
        jsonMessage.put("orderType", orderEntity.getOrderType());
        jsonMessage.put("username", orderEntity.getUsername());
        jsonMessage.put("warehouseReservationID", orderEntity.getWarehouseReservationID());
        jsonMessage.put("cost", orderEntity.getCost());
        String message = jsonMessage.toString();
        this.template.convertAndSend(accountingInputOrchestrationQueue.getName(), message);
        this.log("[OrderService::sendAccountingValidate] sent " + message);
    }

    @Override
    public void log(String message) {
        this.template.convertAndSend(logInputQueue.getName(), message);
    }
}
