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
        System.out.println(" [accounting service] Received receiveCreateAppointmentOrchestration start '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        int warehouseAppointmentID = jsonObject.getInt("warehouseReservationID");
        int cost = jsonObject.getInt("cost");
        String username = jsonObject.getString("username");
        Optional<AccountingTransactionEntity> accountingTransaction = accountingService.createAndValidateOrderOrchestration(orderID, orderType, warehouseAppointmentID, cost, username);
        sendResponseOrchestration(orderID, accountingTransaction);
        System.out.println(" [accounting service] Received receiveCreateAppointmentOrchestration end '" + in + "'");
    }

    @RabbitListener(queues = "#{accountingOrderOutputQueue.name}")
    public void receiveCreateAppointment(String in) {
        System.out.println(" [accounting service] Received receiveCreateAppointment start '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        AccountingTransactionStatusEnum status = accountingService.createOrValidateOrder(orderID, orderType);
        sendResponseChoreography(orderID, status);
        System.out.println(" [accounting service] Received receiveCreateAppointment end'" + in + "'");
    }

    @RabbitListener(queues = "warehouse_output_choreography")
    public void receiveWarehouseOutput(String in) {
        System.out.println(" [accounting service] Received receiveWarehouseOutput start'" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int warehouseAppointmentID = jsonObject.optInt("warehouseReservationID");
        int cost = jsonObject.optInt("cost");
        boolean validated = jsonObject.getBoolean("validated");
        AccountingTransactionStatusEnum status = accountingService.createOrValidateWarehouse(orderID, warehouseAppointmentID, cost, validated);
        sendResponseChoreography(orderID, status);
        System.out.println(" [accounting service] Received receiveWarehouseOutput end'" + in + "'");
    }

    @RabbitListener(queues = "user_output_choreography")
    public void receiveUserOutput(String in) {
        System.out.println(" [accounting service] Received receiveUserOutput start'" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.optString("username");
        boolean validated = jsonObject.getBoolean("validated");
        AccountingTransactionStatusEnum status = accountingService.createOrValidateUser(orderID, username, validated);
        sendResponseChoreography(orderID, status);
        System.out.println(" [accounting service] Received receiveUserOutput end'" + in + "'");
    }

    private void sendResponseChoreography(int orderID, AccountingTransactionStatusEnum status) {
        if (status == AccountingTransactionStatusEnum.REJECTED) {
            accountingSender.sendFailureChoreography(orderID);
        }
        else if (status == AccountingTransactionStatusEnum.FINALIZED) {
            accountingSender.sendSuccessChoreography(orderID, accountingService.getTransaction(orderID));
        }
    }

    private void sendResponseOrchestration(int orderID, Optional<AccountingTransactionEntity> accountingTransaction) {
        if (accountingTransaction.isPresent()) {
            accountingSender.sendSuccessOrchestration(orderID, accountingTransaction.get());
        }
        else {
            accountingSender.sendFailureOrchestration(orderID);
        }
    }
}
