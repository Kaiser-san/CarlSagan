package com.ldjuric.saga.accounting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Profile({"accounting", "all"})
@Service
public class AccountingService {
    @Autowired
    private AccountingRepository accountingRepository;
    @Autowired
    private AccountingTransactionRepository accountingTransactionRepository;

    @Autowired
    private AccountingTransactionVersionFileRepository accountingTransactionVersionFileRepository;

    @Autowired
    private AccountingMQSender sender;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<AccountingTransactionEntity> createAndValidateOrderOrchestration(int orderID, int orderType, int cost, String username) {
        sender.log("[AccountingService::createAndValidateOrderOrchestration] start");
        AccountingTransactionEntity transactionEntity = new AccountingTransactionEntity();
        transactionEntity.setOrderID(orderID);
        transactionEntity.setOrderType(orderType);
        transactionEntity.setCost(cost);

        Optional<AccountingEntity> accountingEntity = accountingRepository.findByUsername(username);
        if (accountingEntity.isPresent()) {
            sender.log("[AccountingService::createAndValidateOrderOrchestration] enough credit; orderID:" + orderID);
            transactionEntity.setAccountingEntity(accountingEntity.get());
        }
        else {
            sender.log("[AccountingService::createAndValidateOrderOrchestration] not enough credit; orderID:" + orderID);
            return Optional.empty();
        }

        accountingTransactionRepository.save(transactionEntity);
        sender.log("[AccountingService::createAndValidateOrderOrchestration] saved transaction; orderID:" + orderID);

        return Optional.of(transactionEntity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AccountingTransactionStatusEnum createOrValidateOrder(int orderID, int orderType) {
        sender.log("[AccountingService::createOrValidateOrder] start");
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            sender.log("[AccountingService::createOrValidateOrder] already rejected; orderID:" + orderID);
            return AccountingTransactionStatusEnum.INITIALIZING;
        }

        transactionVersionEntity.get().setOrderType(orderType);
        accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
        sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);

        return this.validateTransaction(orderID);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AccountingTransactionStatusEnum createOrValidateWarehouse(int orderID, int cost, boolean validated) {
        sender.log("[AccountingService::createOrValidateOrder] start; orderID:" + orderID);
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            sender.log("[AccountingService::createOrValidateOrder] already rejected; orderID:" + orderID);
            return AccountingTransactionStatusEnum.INITIALIZING;
        }

        transactionVersionEntity.get().setCost(cost);
        accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
        sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);

        if (!validated) {
            sender.log("[AccountingService::createOrValidateOrder] invalid warehouse stock, rejecting; orderID:" + orderID);
            return AccountingTransactionStatusEnum.REJECTED;
        }

        return this.validateTransaction(orderID);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AccountingTransactionStatusEnum createOrValidateUser(int orderID, String username, boolean validated) {
        sender.log("[AccountingService::createOrValidateOrder] start; orderID:" + orderID);
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            sender.log("[AccountingService::createOrValidateOrder] already rejected; orderID:" + orderID);
            return AccountingTransactionStatusEnum.INITIALIZING;
        }

        if (!validated) {
            sender.log("[AccountingService::createOrValidateOrder] user invalid, reject; orderID:" + orderID);
            transactionVersionEntity.get().setStatus(AccountingTransactionStatusEnum.REJECTED);
            accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
            sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);
            return AccountingTransactionStatusEnum.REJECTED;
        }

        Optional<AccountingEntity> accountingEntity = accountingRepository.findByUsername(username);
        if (accountingEntity.isPresent()) {
            sender.log("[AccountingService::createOrValidateOrder] user account found; orderID:" + orderID);
            transactionVersionEntity.get().setAccountingEntity(accountingEntity.get());
            accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
            sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);
        }
        else {
            sender.log("[AccountingService::createOrValidateOrder] no user account found, reject; orderID:" + orderID);
            transactionVersionEntity.get().setStatus(AccountingTransactionStatusEnum.REJECTED);
            accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
            sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);
            return AccountingTransactionStatusEnum.REJECTED;
        }

        return this.validateTransaction(orderID);
    }

    public AccountingTransactionEntity getTransaction(int orderID) {
        Optional<AccountingTransactionEntity> accountingTransaction = accountingTransactionRepository.findByOrderID(orderID);
        return accountingTransaction.orElse(null);
    }

    private AccountingTransactionStatusEnum validateTransaction(int orderID) {
        sender.log("[AccountingService::validateTransaction] start; orderID:" + orderID);
        List<AccountingTransactionVersionFileEntity> versionFileEntities = accountingTransactionVersionFileRepository.findAllByOrderID(orderID);
        Integer orderType = null;
        AccountingEntity accountingEntity = null;
        Integer cost = null;

        for (AccountingTransactionVersionFileEntity versionFileEntity : versionFileEntities) {
            if (versionFileEntity.getStatus() == AccountingTransactionStatusEnum.REJECTED) {
                sender.log("[AccountingService::validateTransaction] already rejected, don't send; orderID:" + orderID);
                return AccountingTransactionStatusEnum.INITIALIZING;
            }
            if (versionFileEntity.getStatus() == AccountingTransactionStatusEnum.FINALIZED) {
                sender.log("[AccountingService::validateTransaction] already finalized, don't send; orderID:" + orderID);
                return AccountingTransactionStatusEnum.INITIALIZING;
            }

            if (versionFileEntity.getOrderType() != null) {
                orderType = versionFileEntity.getOrderType();
            }
            if (versionFileEntity.getAccountingEntity() != null) {
                accountingEntity = versionFileEntity.getAccountingEntity();
            }
            if (versionFileEntity.getCost() != null) {
                cost = versionFileEntity.getCost();
            }
        }

        if (orderType != null
                && accountingEntity != null
                && cost != null) {

            if (accountingEntity.getCredit() < cost) {
                sender.log("[AccountingService::validateTransaction] not enough money, reject; orderID:" + orderID);

                for (AccountingTransactionVersionFileEntity versionFileEntity : versionFileEntities) {
                    versionFileEntity.setStatus(AccountingTransactionStatusEnum.REJECTED);
                }
                accountingTransactionVersionFileRepository.saveAll(versionFileEntities);
                sender.log("[AccountingService::createOrValidateOrder] saved version files; orderID:" + orderID);

                return AccountingTransactionStatusEnum.REJECTED;
            }

            for (AccountingTransactionVersionFileEntity versionFileEntity : versionFileEntities) {
                versionFileEntity.setStatus(AccountingTransactionStatusEnum.FINALIZED);
            }

            accountingTransactionVersionFileRepository.saveAll(versionFileEntities);
            sender.log("[AccountingService::createOrValidateOrder] saved version files; orderID:" + orderID);

            accountingEntity.setCredit(accountingEntity.getCredit() - cost);
            accountingRepository.save(accountingEntity);
            sender.log("[AccountingService::createOrValidateOrder] saved accounting; orderID:" + orderID);

            AccountingTransactionEntity transactionEntity = new AccountingTransactionEntity();
            transactionEntity.setOrderID(orderID);
            transactionEntity.setOrderType(orderType);
            transactionEntity.setCost(cost);
            transactionEntity.setAccountingEntity(accountingEntity);
            accountingTransactionRepository.save(transactionEntity);
            sender.log("[AccountingService::createOrValidateOrder] saved transaction; orderID:" + orderID);

            return AccountingTransactionStatusEnum.FINALIZED;
        }
        return AccountingTransactionStatusEnum.INITIALIZING;
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
