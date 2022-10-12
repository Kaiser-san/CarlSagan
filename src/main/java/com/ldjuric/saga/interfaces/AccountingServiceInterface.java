package com.ldjuric.saga.interfaces;

import com.ldjuric.saga.accounting.AccountingTransactionEntity;

import java.util.Optional;

public interface AccountingServiceInterface {
    Optional<AccountingTransactionEntity> createOrValidateOrder(int orderID, int orderType);

    Optional<AccountingTransactionEntity> createOrValidateKitchen(int orderID, int kitchenAppointmentID, boolean validated);

    Optional<AccountingTransactionEntity> createOrValidateUser(int orderID, String username, boolean validated);
}
