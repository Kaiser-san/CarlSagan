package com.ldjuric.saga.order;

import com.ldjuric.saga.interfaces.OrderServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderMQReceiver {
    @Autowired
    private OrderServiceInterface orderService;

    @Autowired
    private OrderCreateOrchestrator orderCreateOrchestrator;

    @RabbitListener(queues = "#{orderAccountingOutputQueue.name}")
    public void receiveAccountingOutputChoreography(String in) {
        System.out.println(" [order service] Received '" + in + "'");
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
        System.out.println(" [order service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.optString("username");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.userValidated(orderID, username, validated);
    }

    @RabbitListener(queues = "warehouse_output_orchestration")
    public void receiveWarehouseOutput(String in) {
        System.out.println(" [order service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int warehouseReservationID = jsonObject.optInt("warehouseReservationID");
        int cost = jsonObject.optInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.warehouseValidated(orderID, warehouseReservationID, cost, validated);
    }

    @RabbitListener(queues = "accounting_output_orchestration")
    public void receiveAccountingOutputOrchestration(String in) {
        System.out.println(" [order service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int accountingTransactionID = jsonObject.optInt("accountingTransactionID");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.accountingValidated(orderID, accountingTransactionID, validated);
    }
}
