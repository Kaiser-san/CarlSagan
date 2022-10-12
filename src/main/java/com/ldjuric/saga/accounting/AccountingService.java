package com.ldjuric.saga.accounting;

import com.ldjuric.saga.interfaces.AccountingServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountingService implements AccountingServiceInterface {
    @Autowired
    private AccountingRepository accountingRepository;
    @Autowired
    private AccountingTransactionRepository accountingTransactionRepository;

    @Override
    public Optional<AccountingTransactionEntity> createOrValidateOrder(int orderID, int orderType) {
        Optional<AccountingTransactionEntity> transactionEntity = this.getOrCreateTransactionEntity(orderID);
        if (transactionEntity.isEmpty()) {
            return transactionEntity;
        }

        this.validateTransaction(transactionEntity.get());

        return transactionEntity;
    }

    @Override
    public Optional<AccountingTransactionEntity> createOrValidateKitchen(int orderID, int kitchenAppointmentID, boolean validated) {
        Optional<AccountingTransactionEntity> transactionEntity = this.getOrCreateTransactionEntity(orderID);
        if (transactionEntity.isEmpty()) {
            return transactionEntity;
        }

        transactionEntity.get().setKitchenAppointmentID(kitchenAppointmentID);

        if (!validated)
        {
            transactionEntity.get().setStatus(AccountingTransactionStatusEnum.REJECTED);
            return transactionEntity;
        }

        transactionEntity.get().setKitchenAppointmentID(kitchenAppointmentID);

        this.validateTransaction(transactionEntity.get());

        return transactionEntity;
    }

    @Override
    public Optional<AccountingTransactionEntity> createOrValidateUser(int orderID, String username, boolean validated) {
        Optional<AccountingTransactionEntity> transactionEntity = this.getOrCreateTransactionEntity(orderID);
        if (transactionEntity.isEmpty()) {
            return transactionEntity;
        }

        if (!validated)
        {
            transactionEntity.get().setStatus(AccountingTransactionStatusEnum.REJECTED);
            return transactionEntity;
        }

        Optional<AccountingEntity> accountingEntity = accountingRepository.findByUsername(username);
        if (accountingEntity.isPresent()) {
            transactionEntity.get().setAccountingEntity(accountingEntity.get());
        }
        else {
            transactionEntity.get().setStatus(AccountingTransactionStatusEnum.REJECTED);
            return transactionEntity;
        }

        this.validateTransaction(transactionEntity.get());

        return transactionEntity;
    }

    private AccountingTransactionStatusEnum validateTransaction(AccountingTransactionEntity accountingTransaction) {
        if (accountingTransaction.getOrderType() != 0
                && accountingTransaction.getAccountingEntity() != null
                && accountingTransaction.getKitchenAppointmentID() != 0
                && accountingTransaction.getAccountingEntity().getCredit() > accountingTransaction.getCost()) {
            accountingTransaction.getAccountingEntity().setCredit(accountingTransaction.getAccountingEntity().getCredit() - accountingTransaction.getCost());
            return AccountingTransactionStatusEnum.FINALIZED;
        }
        return AccountingTransactionStatusEnum.INITIALIZING;
    }

    private Optional<AccountingTransactionEntity> getOrCreateTransactionEntity(int orderID) {
        Optional<AccountingTransactionEntity> transactionEntityRejected = accountingTransactionRepository.findByOrderID(orderID, AccountingTransactionStatusEnum.REJECTED);
        if (transactionEntityRejected.isPresent()) {
            return Optional.empty();
        }

        Optional<AccountingTransactionEntity> transactionEntityInitializing = accountingTransactionRepository.findByOrderID(orderID, AccountingTransactionStatusEnum.INITIALIZING);
        AccountingTransactionEntity transactionEntity;
        if (transactionEntityInitializing.isEmpty()) {
            transactionEntity = new AccountingTransactionEntity();
            transactionEntity.setOrderID(orderID);
            transactionEntityInitializing = Optional.of(transactionEntity);
        }

        return transactionEntityInitializing;
    }
}
