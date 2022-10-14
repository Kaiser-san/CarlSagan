package com.ldjuric.saga.interfaces;

import com.ldjuric.saga.accounting.AccountingTransactionEntity;
import com.ldjuric.saga.accounting.AccountingTransactionStatusEnum;

import java.util.Optional;

public interface AccountingServiceInterface {
    Optional<AccountingTransactionEntity> createAndValidateOrderOrchestration(int orderID, int orderType, int warehouseReservationID, int cost, String username);

    AccountingTransactionStatusEnum createOrValidateOrder(int orderID, int orderType);

    AccountingTransactionStatusEnum createOrValidateWarehouse(int orderID, int warehouseReservationID, int cost, boolean validated);

    AccountingTransactionStatusEnum createOrValidateUser(int orderID, String username, boolean validated);

    AccountingTransactionEntity getTransaction(int orderID);
}
