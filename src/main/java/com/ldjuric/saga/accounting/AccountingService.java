package com.ldjuric.saga.accounting;

import com.ldjuric.saga.interfaces.AccountingServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Profile({"accounting", "all"})
@Service
public class AccountingService implements AccountingServiceInterface {
    @Autowired
    private AccountingRepository accountingRepository;
    @Autowired
    private AccountingTransactionRepository accountingTransactionRepository;

    @Autowired
    private AccountingTransactionVersionFileRepository accountingTransactionVersionFileRepository;

    @Autowired
    private AccountingMQSender sender;

    public String getAccounts() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterable<AccountingEntity> accountingEntities = accountingRepository.findAll();
        for (AccountingEntity accountingEntity : accountingEntities) {
            stringBuilder.append(accountingEntity.toString());
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }

    public String getAccount(String username) {
        Optional<AccountingEntity> accountingEntity = accountingRepository.findByUsername(username);
        return accountingEntity.isPresent() ? accountingEntity.get().toString() : "";
    }

    public String getTransactions() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterable<AccountingTransactionEntity> accountingTransactionEntities = accountingTransactionRepository.findAll();
        for (AccountingTransactionEntity accountingTransactionEntity : accountingTransactionEntities) {
            stringBuilder.append(accountingTransactionEntity.toString());
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }

    public String getVersionFiles() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterable<AccountingTransactionVersionFileEntity> versionFileEntities = accountingTransactionVersionFileRepository.findAll();
        for (AccountingTransactionVersionFileEntity versionFile : versionFileEntities) {
            stringBuilder.append(versionFile.toString());
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }

    public boolean createAccount(String username, Integer credit) {
        Optional<AccountingEntity> existingAccountingEntity = accountingRepository.findByUsername(username);
        if (existingAccountingEntity.isPresent()) {
            return false;
        }

        AccountingEntity accountingEntity = new AccountingEntity();
        accountingEntity.setUsername(username);
        accountingEntity.setCredit(credit);
        accountingRepository.save(accountingEntity);
        return true;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void validateOrderOrchestration(Integer orderID, Integer orderType, String username, Integer cost) {
        sender.log("[AccountingService::createAndValidateOrderOrchestration] start");
        AccountingTransactionEntity transactionEntity = new AccountingTransactionEntity();
        transactionEntity.setOrderID(orderID);
        transactionEntity.setOrderType(orderType);
        transactionEntity.setCost(cost);

        Optional<AccountingEntity> accountingEntity = accountingRepository.findByUsername(username);
        if (accountingEntity.isPresent() && accountingEntity.get().getCredit() > cost) {
            sender.log("[AccountingService::createAndValidateOrderOrchestration] enough credit; orderID:" + orderID);
            transactionEntity.setAccountingEntity(accountingEntity.get());
            accountingEntity.get().setCredit(accountingEntity.get().getCredit() - cost);
            accountingRepository.save(accountingEntity.get());
        }
        else {
            sender.log("[AccountingService::createAndValidateOrderOrchestration] not enough credit; orderID:" + orderID);
            sender.sendFailureOrchestration(orderID);
        }

        accountingTransactionRepository.save(transactionEntity);
        sender.log("[AccountingService::createAndValidateOrderOrchestration] saved transaction; orderID:" + orderID);

        sender.sendSuccessOrchestration(orderID, transactionEntity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrValidateOrder(int orderID, int orderType) {
        sender.log("[AccountingService::createOrValidateOrder] start");
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            sender.log("[AccountingService::createOrValidateOrder] already rejected; orderID:" + orderID);
            return;
        }

        transactionVersionEntity.get().setOrderType(orderType);
        accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
        sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);

        sendResponseChoreography(orderID, this.validateTransaction(orderID));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrValidateWarehouse(int orderID, int cost, boolean validated) {
        sender.log("[AccountingService::createOrValidateOrder] start; orderID:" + orderID);
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            sender.log("[AccountingService::createOrValidateOrder] already rejected; orderID:" + orderID);
            return;
        }

        transactionVersionEntity.get().setCost(cost);
        accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
        sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);

        if (!validated) {
            sender.log("[AccountingService::createOrValidateOrder] invalid warehouse stock, rejecting; orderID:" + orderID);
            sender.sendFailureChoreography(orderID);
        }

        sendResponseChoreography(orderID, this.validateTransaction(orderID));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrValidateUser(int orderID, String username, boolean validated) {
        sender.log("[AccountingService::createOrValidateOrder] start; orderID:" + orderID);
        Optional<AccountingTransactionVersionFileEntity> transactionVersionEntity = this.getOrCreateTransactionEntityVersionFile(orderID);
        if (transactionVersionEntity.isEmpty()) {
            sender.log("[AccountingService::createOrValidateOrder] already rejected; orderID:" + orderID);
            return;
        }

        if (!validated) {
            sender.log("[AccountingService::createOrValidateOrder] user invalid, reject; orderID:" + orderID);
            transactionVersionEntity.get().setStatus(AccountingTransactionStatusEnum.REJECTED);
            accountingTransactionVersionFileRepository.save(transactionVersionEntity.get());
            sender.log("[AccountingService::createOrValidateOrder] saved version file; orderID:" + orderID);
            sender.sendFailureChoreography(orderID);
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
            sender.sendFailureChoreography(orderID);
        }

        sendResponseChoreography(orderID, this.validateTransaction(orderID));
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

        if (orderType != null && accountingEntity != null && cost != null) {
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

    private void sendResponseChoreography(int orderID, AccountingTransactionStatusEnum status) {
        if (status == AccountingTransactionStatusEnum.REJECTED) {
            sender.sendFailureChoreography(orderID);
        }
        else if (status == AccountingTransactionStatusEnum.FINALIZED) {
            sender.sendSuccessChoreography(orderID, getTransaction(orderID));
        }
    }
}
