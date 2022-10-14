package com.ldjuric.saga.order;

import com.ldjuric.saga.accounting.AccountingMQSender;
import com.ldjuric.saga.interfaces.OrderServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderMQReceiver {
    @Autowired
    private OrderServiceInterface orderService;

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
        int kitchenAppointmentID = jsonObject.optInt("warehouseReservationID");
        int cost = jsonObject.optInt("cost");
        int accountingTransactionID = jsonObject.optInt("accountingTransactionID");
        boolean validated = jsonObject.getBoolean("validated");
        orderService.accountingValidatedChoreography(orderID, username, kitchenAppointmentID, cost, accountingTransactionID, validated);
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
        int warehouseReservationID = jsonObject.optInt("warehouseReservationID");
        int cost = jsonObject.optInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.warehouseValidated(orderID, warehouseReservationID, cost, validated);
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
