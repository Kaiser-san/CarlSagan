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

    @RabbitListener(queues = "accounting_input_orchestration")
    public void receiveCreateAppointmentOrchestration(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        int warehouseAppointmentID = jsonObject.getInt("warehouseReservationID");
        int cost = jsonObject.getInt("cost");
        String username = jsonObject.get("username").toString();
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createAndValidateOrderOrchestration(orderID, orderType, warehouseAppointmentID, cost, username);
        sendResponseOrchestration(orderID, accountingTransaction);
    }

    @RabbitListener(queues = "order_output")
    public void receiveCreateAppointment(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateOrder(orderID, orderType);
        sendResponseChoreography(orderID, accountingTransaction);
    }

    @RabbitListener(queues = "warehouse_output")
    public void receiveWarehouseOutput(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int warehouseAppointmentID = jsonObject.getInt("warehouseReservationID");
        int cost = jsonObject.getInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateWarehouse(orderID, warehouseAppointmentID, cost, validated);
        sendResponseChoreography(orderID, accountingTransaction);
    }

    @RabbitListener(queues = "user_output")
    public void receiveUserOutput(String in) {
        System.out.println(" [accounting service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.get("username").toString();
        boolean validated = jsonObject.getBoolean("validated");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createOrValidateUser(orderID, username, validated);
        sendResponseChoreography(orderID, accountingTransaction);
    }

    private void sendResponseChoreography(int orderID, Optional<AccountingTransactionEntity> accountingTransaction) {
        if (accountingTransaction.isPresent()) {
            if (accountingTransaction.get().getStatus() == AccountingTransactionStatusEnum.REJECTED) {
                accountingSender.sendFailureChoreography(orderID);
            }
            else if (accountingTransaction.get().getStatus() == AccountingTransactionStatusEnum.FINALIZED) {
                accountingSender.sendSuccessChoreography(orderID, accountingTransaction.get());
            }
        }
    }

    private void sendResponseOrchestration(int orderID, Optional<AccountingTransactionEntity> accountingTransaction) {
        if (accountingTransaction.isPresent()) {
            if (accountingTransaction.get().getStatus() == AccountingTransactionStatusEnum.REJECTED) {
                accountingSender.sendFailureOrchestration(orderID);
            }
            else if (accountingTransaction.get().getStatus() == AccountingTransactionStatusEnum.FINALIZED) {
                accountingSender.sendSuccessOrchestration(orderID, accountingTransaction.get());
            }
        }
    }
}
