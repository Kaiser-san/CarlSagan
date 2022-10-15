package com.ldjuric.saga.order;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

@Profile({"order", "all"})
public class OrderMQReceiver {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderCreateOrchestrator orderCreateOrchestrator;

    @Autowired
    private OrderMQSender sender;

    @RabbitListener(queues = "#{orderAccountingOutputQueue.name}")
    public void receiveAccountingOutputChoreography(String in) {
        sender.log("[OrderService::receiveAccountingOutputChoreography] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.optString("username");
        int cost = jsonObject.optInt("cost");
        int accountingTransactionID = jsonObject.optInt("accountingTransactionID");
        boolean validated = jsonObject.getBoolean("validated");
        orderService.accountingValidatedChoreography(orderID, username, cost, accountingTransactionID, validated);
    }

    @RabbitListener(queues = "user_output_orchestration")
    public void receiveUserOutput(String in) {
        sender.log("[OrderService::receiveUserOutput] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.optString("username");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.userValidated(orderID, username, validated);
    }

    @RabbitListener(queues = "warehouse_output_orchestration")
    public void receiveWarehouseOutput(String in) {
        sender.log("[OrderService::receiveWarehouseOutput] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int cost = jsonObject.optInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.warehouseValidated(orderID, cost, validated);
    }

    @RabbitListener(queues = "accounting_output_orchestration")
    public void receiveAccountingOutputOrchestration(String in) {
        sender.log("[OrderService::receiveAccountingOutputOrchestration] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int accountingTransactionID = jsonObject.optInt("accountingTransactionID");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.accountingValidated(orderID, accountingTransactionID, validated);
    }
}
