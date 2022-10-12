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

    @RabbitListener(queues = "accounting_create_orchestration")
    public void receiveCreateAppointmentOrchestration(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int orderType = jsonObject.getInt("order_type");
        int kitchenAppointmentID = jsonObject.getInt("kitchen_appointment_id");
        int cost = jsonObject.getInt("cost");
        String username = jsonObject.get("username").toString();
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createAndValidateOrderOrchestration(orderID, orderType, kitchenAppointmentID, cost, username);
        sendResponse(orderID, accountingTransaction);
    }

    @RabbitListener(queues = "accounting_create")
    public void receiveCreateAppointment(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int orderType = jsonObject.getInt("order_type");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateOrder(orderID, orderType);
        sendResponse(orderID, accountingTransaction);
    }

    @RabbitListener(queues = "kitchen_output")
    public void receiveKitchenOutput(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int kitchenAppointmentID = jsonObject.getInt("kitchen_appointment_id");
        int cost = jsonObject.getInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateKitchen(orderID, kitchenAppointmentID, cost, validated);
        sendResponse(orderID, accountingTransaction);
    }

    @RabbitListener(queues = "user_output")
    public void receiveUserOutput(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        String username = jsonObject.get("username").toString();
        boolean validated = jsonObject.getBoolean("validated");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateUser(orderID, username, validated);
        sendResponse(orderID, accountingTransaction);
    }

    private void sendResponse(int orderID, Optional<AccountingTransactionEntity> accountingTransaction) {
        if (accountingTransaction.isPresent()) {
            if (accountingTransaction.get().getStatus() == AccountingTransactionStatusEnum.REJECTED) {
                accountingSender.sendFailure(orderID);
            }
            else if (accountingTransaction.get().getStatus() == AccountingTransactionStatusEnum.FINALIZED) {
                accountingSender.sendSuccess(orderID, accountingTransaction.get().getId());
            }
        }
    }
}
