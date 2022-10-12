package com.ldjuric.saga.accounting;

import com.ldjuric.saga.interfaces.AccountingServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AccountingMQReceiver {
    @Autowired
    private AccountingServiceInterface accountingService;

    @Autowired
    private AccountingMQSender accountingSender;

    @RabbitListener(queues = "accounting_create")
    public void receiveCreateAppointment(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int orderType = jsonObject.getInt("order_type");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateOrder(orderID, orderType);
    }

    @RabbitListener(queues = "kitchen_output")
    public void receiveKitchenOutput(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int kitchenAppointmentID = jsonObject.getInt("kitchen_appointment_id");
        boolean validated = jsonObject.getBoolean("validated");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateKitchen(orderID, kitchenAppointmentID, validated);
    }

    @RabbitListener(queues = "user_output")
    public void receiveUserOutput(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        String username = jsonObject.get("username").toString();
        boolean validated = jsonObject.getBoolean("validated");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateUser(orderID, username, validated);
    }
}
