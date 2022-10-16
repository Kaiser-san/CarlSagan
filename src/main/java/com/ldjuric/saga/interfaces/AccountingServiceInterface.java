package com.ldjuric.saga.interfaces;

import com.ldjuric.saga.accounting.AccountingTransactionEntity;
import com.ldjuric.saga.accounting.AccountingTransactionStatusEnum;
import com.ldjuric.saga.order.OrderEntity;

import java.util.Optional;

public interface AccountingServiceInterface {

    void validateOrderOrchestration(Integer orderID, Integer orderType, String username, Integer cost);
}
