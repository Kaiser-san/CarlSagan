package com.ldjuric.saga.order;

import com.ldjuric.saga.logs.interfaces.OrderServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderMQReceiver {
    @Autowired
    private OrderServiceInterface orderService;

    @Autowired
    private OrderCreateOrchestrator orderCreateOrchestrator;

    @RabbitListener(queues = "accounting_output_choreography")
    public void receiveAccountingOutputChoreography(String in) {
        System.out.println(" [order service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        String username = jsonObject.get("username").toString();
        int kitchenAppointmentID = jsonObject.getInt("kitchen_appointment_id");
        int cost = jsonObject.getInt("cost");
        int accountingTransactionID = jsonObject.getInt("accounting_transaction_id");
        boolean validated = jsonObject.getBoolean("validated");
        orderService.accountingValidatedChoreography(orderID, username, kitchenAppointmentID, cost, accountingTransactionID, validated);
    }

    @RabbitListener(queues = "user_output")
    public void receiveUserOutput(String in) {
        System.out.println(" [order service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        String username = jsonObject.get("username").toString();
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.userValidated(orderID, username, validated);
    }

    @RabbitListener(queues = "kitchen_output")
    public void receiveKitchenOutput(String in) {
        System.out.println(" [order service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int kitchenAppointmentID = jsonObject.getInt("kitchen_appointment_id");
        int cost = jsonObject.getInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.kitchenValidated(orderID, kitchenAppointmentID, cost, validated);
    }

    @RabbitListener(queues = "accounting_output_orchestration")
    public void receiveAccountingOutputOrchestration(String in) {
        System.out.println(" [order service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int accountingTransactionID = jsonObject.getInt("accounting_transaction_id");
        boolean validated = jsonObject.getBoolean("validated");
        orderCreateOrchestrator.accountingValidated(orderID, accountingTransactionID, validated);
    }
}
