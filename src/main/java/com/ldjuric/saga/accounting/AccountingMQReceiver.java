package com.ldjuric.saga.accounting;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

@Profile({"accounting", "all"})
public class AccountingMQReceiver {
    @Autowired
    private AccountingService accountingService;

    @Autowired
    private AccountingMQSender sender;

    @RabbitListener(queues = "accounting_input_orchestration")
    public void receiveCreateOrderOrchestration(String in) {
        sender.log("[AccountingService::receiveCreateAppointmentOrchestration] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        int cost = jsonObject.getInt("cost");
        String username = jsonObject.getString("username");
        accountingService.validateOrderOrchestration(orderID, orderType, username, cost);
    }

    @RabbitListener(queues = "#{accountingOrderOutputQueue.name}")
    public void receiveOrderOutputChoreography(String in) {
        sender.log("[AccountingService::receiveCreateAppointment] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        accountingService.createOrValidateOrder(orderID, orderType);
    }

    @RabbitListener(queues = "warehouse_output_choreography")
    public void receiveWarehouseOutputChoreography(String in) {
        sender.log("[AccountingService::receiveWarehouseOutput] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int cost = jsonObject.optInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        accountingService.createOrValidateWarehouse(orderID, cost, validated);
    }

    @RabbitListener(queues = "user_output_choreography")
    public void receiveUserOutputChoreography(String in) {
        sender.log("[AccountingService::receiveUserOutput] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.optString("username");
        boolean validated = jsonObject.getBoolean("validated");
        accountingService.createOrValidateUser(orderID, username, validated);
    }
}
