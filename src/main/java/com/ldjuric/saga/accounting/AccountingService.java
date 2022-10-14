package com.ldjuric.saga.accounting;

import com.ldjuric.saga.interfaces.AccountingServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountingService implements AccountingServiceInterface {
    @Autowired
    private AccountingRepository accountingRepository;
    @Autowired
    private AccountingTransactionRepository accountingTransactionRepository;

    @Autowired
    private AccountingTransactionVersionFileRepository accountingTransactionVersionFileRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<AccountingTransactionEntity> createAndValidateOrderOrchestration(int orderID, int orderType, int warehouseReservationID, int cost, String username) {
        Optional<AccountingTransactionEntity> transactionEntity = this.getOrCreateTransactionEntity(orderID);
        if (transactionEntity.isEmpty()) {
            return Optional.empty();
        }

        transactionEntity.get().setOrderType(orderType);
        transactionEntity.get().setWarehouseReservationID(warehouseReservationID);
        transactionEntity.get().setCost(cost);
        
        Optional<AccountingEntity> accountingEntity = accountingRepository.findByUsername(username);
        if (accountingEntity.isPresent()) {
            transactionEntity.get().setAccountingEntity(accountingEntity.get());
        }
        else {
            return Optional.empty();
        }

        accountingTransactionRepository.save(transactionEntity.get());

        return transactionEntity;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AccountingTransactionStatusEnum createOrValidateOrder(int orderID, int orderType) {
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            return AccountingTransactionStatusEnum.INITIALIZING;
        }

        transactionVersionEntity.get().setOrderType(orderType);
        accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());

        return this.validateTransaction(orderID);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AccountingTransactionStatusEnum createOrValidateWarehouse(int orderID, int warehouseReservationID, int cost, boolean validated) {
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            return AccountingTransactionStatusEnum.INITIALIZING;
        }

        transactionVersionEntity.get().setWarehouseReservationID(warehouseReservationID);
        transactionVersionEntity.get().setCost(cost);
        accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());

        if (!validated) {
            return AccountingTransactionStatusEnum.REJECTED;
        }

        return this.validateTransaction(orderID);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AccountingTransactionStatusEnum createOrValidateUser(int orderID, String username, boolean validated) {
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            return AccountingTransactionStatusEnum.INITIALIZING;
        }

        if (!validated) {
            return AccountingTransactionStatusEnum.REJECTED;
        }

        Optional<AccountingEntity> accountingEntity = accountingRepository.findByUsername(username);
        if (accountingEntity.isPresent()) {
            transactionVersionEntity.get().setAccountingEntity(accountingEntity.get());
        }
        else {
            return AccountingTransactionStatusEnum.REJECTED;
        }
        accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());

        return this.validateTransaction(orderID);
    }

    @Override
    public AccountingTransactionEntity getTransaction(int orderID) {
        Optional<AccountingTransactionEntity> accountingTransaction = accountingTransactionRepository.findByOrderID(orderID);
        return accountingTransaction.orElse(null);
    }

    private AccountingTransactionStatusEnum validateTransaction(int orderID) {
        List<AccountingTransactionVersionFileEntity> versionFileEntities = accountingTransactionVersionFileRepository.findAllByOrderID(orderID);
        Integer orderType = null;
        AccountingEntity accountingEntity = null;
        Integer warehouseReservationID = null;
        Integer cost = null;

        for (AccountingTransactionVersionFileEntity versionFileEntity : versionFileEntities) {
            System.out.println("VersionFileEntity: " + versionFileEntity.toString());
            if (versionFileEntity.getStatus() == AccountingTransactionStatusEnum.REJECTED) {
                return AccountingTransactionStatusEnum.REJECTED;
            }
            if (versionFileEntity.getStatus() == AccountingTransactionStatusEnum.FINALIZED) {
                return AccountingTransactionStatusEnum.INITIALIZING;
            }

            if (versionFileEntity.getOrderType() != null) {
                orderType = versionFileEntity.getOrderType();
            }
            if (versionFileEntity.getAccountingEntity() != null) {
                accountingEntity = versionFileEntity.getAccountingEntity();
            }
            if (versionFileEntity.getWarehouseReservationID() != null) {
                warehouseReservationID = versionFileEntity.getWarehouseReservationID();
            }
            if (versionFileEntity.getCost() != null) {
                cost = versionFileEntity.getCost();
            }
        }

        if (orderType != null
                && accountingEntity != null
                && warehouseReservationID != null
                && cost != null
                && accountingEntity.getCredit() > cost) {

            for (AccountingTransactionVersionFileEntity versionFileEntity : versionFileEntities) {
                //checking again to make sure something didn't change in the meantime
                if (versionFileEntity.getStatus() == AccountingTransactionStatusEnum.REJECTED) {
                    return AccountingTransactionStatusEnum.REJECTED;
                }
                if (versionFileEntity.getStatus() == AccountingTransactionStatusEnum.FINALIZED) {
                    return AccountingTransactionStatusEnum.INITIALIZING;
                }

                versionFileEntity.setStatus(AccountingTransactionStatusEnum.FINALIZED);
            }

            accountingTransactionVersionFileRepository.saveAll(versionFileEntities);

            accountingEntity.setCredit(accountingEntity.getCredit() - cost);
            accountingRepository.save(accountingEntity);

            AccountingTransactionEntity transactionEntity = new AccountingTransactionEntity();
            transactionEntity.setOrderID(orderID);
            transactionEntity.setOrderType(orderType);
            transactionEntity.setWarehouseReservationID(warehouseReservationID);
            transactionEntity.setCost(cost);
            transactionEntity.setAccountingEntity(accountingEntity);
            accountingTransactionRepository.save(transactionEntity);

            return AccountingTransactionStatusEnum.FINALIZED;
        }
        return AccountingTransactionStatusEnum.INITIALIZING;
    }

    private Optional<AccountingTransactionEntity> getOrCreateTransactionEntity(int orderID) {
        Optional<AccountingTransactionEntity> existingTransactionEntity = accountingTransactionRepository.findByOrderID(orderID);
        //if already added, this means another process finished validating and sent response, so return null so we don't send another one
        if (existingTransactionEntity.isPresent()) {
            return Optional.empty();
        }

        AccountingTransactionEntity transactionEntity = new AccountingTransactionEntity();
        transactionEntity.setOrderID(orderID);
        return Optional.of(transactionEntity);
    }

    private Optional<AccountingTransactionVersionFileEntity> getOrCreateTransactionEntityVersionFile(int orderID) {
        Optional<AccountingTransactionVersionFileEntity> transactionEntityRejected = accountingTransactionVersionFileRepository.findByOrderID(orderID, AccountingTransactionStatusEnum.REJECTED);
        //if already rejected, this means another process finished rejecting and sent response, so return null so we don't send another one
        if (transactionEntityRejected.isPresent()) {
            return Optional.empty();
        }

        AccountingTransactionVersionFileEntity versionFile = new AccountingTransactionVersionFileEntity();
        versionFile.setOrderID(orderID);
        versionFile.setStatus(AccountingTransactionStatusEnum.INITIALIZING);
        return Optional.of(versionFile);
    }
}
