package com.ldjuric.saga.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class OrderCreateOrchestrator {

    @Autowired
    private OrderMQSender sender;

    @Autowired
    private OrderRepository orderRepository;

    public void startOrchestration(Integer orderType, String username, String password) {
        sender.log("[OrderCreateOrchestrator::startOrchestration] start; orderType:" + orderType);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderType(orderType);
        orderEntity.setStatus(OrderStatusEnum.INITIALIZING);
        orderRepository.save(orderEntity);
        Integer orderID = orderEntity.getId();
        sender.log("[OrderCreateOrchestrator::startOrchestration] created order; orderID:" + orderID);
        sender.sendUserValidate(orderID, username, password);
        sender.sendKitchenValidate(orderID, orderType);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void userValidated(int orderID, String username, boolean validated) {
        sender.log("[OrderCreateOrchestrator::userValidated] start; orderID:" + orderID);
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            sender.log("[OrderCreateOrchestrator::userValidated] order deleted, do nothing; orderID:" + orderID);
            return;
        }

        if (!validated) {
            sender.log("[OrderCreateOrchestrator::userValidated] user invalid, reject order; orderID:" + orderID);
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setUsername(username);
        orderRepository.save(orderEntity.get());
        sender.log("[OrderCreateOrchestrator::userValidated] order updated; orderID:" + orderID);

        if (this.validateUserAndWarehouse(orderEntity.get())) {
            sender.log("[OrderCreateOrchestrator::userValidated] order validated; orderID:" + orderID);
            sender.sendAccountingValidate(orderEntity.get());
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void warehouseValidated(int orderID, int cost, boolean validated) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            sender.log("[OrderCreateOrchestrator::warehouseValidated] order deleted, do nothing; orderID:" + orderID);
            return;
        }

        if (!validated) {
            sender.log("[OrderCreateOrchestrator::warehouseValidated] warehouse reservation invalid, reject order; orderID:" + orderID);
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setCost(cost);
        orderRepository.save(orderEntity.get());
        sender.log("[OrderCreateOrchestrator::warehouseValidated] order updated; orderID:" + orderID);

        if (this.validateUserAndWarehouse(orderEntity.get())) {
            sender.log("[OrderCreateOrchestrator::warehouseValidated] order validated; orderID:" + orderID);
            sender.sendAccountingValidate(orderEntity.get());
        }
    }

    public void accountingValidated(int orderID, int accountingTransactionID, boolean validated) {
        sender.log("[OrderCreateOrchestrator::accountingValidated] start; orderID:" + orderID);
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            sender.log("[OrderCreateOrchestrator::accountingValidated] order deleted, do nothing; orderID:" + orderID);
            return;
        }

        if (!validated) {
            sender.log("[OrderCreateOrchestrator::accountingValidated] accounting transaction invalid, reject order; orderID:" + orderID);
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        sender.log("[OrderCreateOrchestrator::accountingValidated] order validated; orderID:" + orderID);
        orderEntity.get().setAccountingTransactionID(accountingTransactionID);
        orderEntity.get().setStatus(OrderStatusEnum.FINALIZED);
        orderRepository.save(orderEntity.get());
        sender.log("[OrderCreateOrchestrator::accountingValidated] order finalized; orderID:" + orderID);
    }

    private boolean validateUserAndWarehouse(OrderEntity orderEntity) {
        return orderEntity.getUsername() != null && !orderEntity.getUsername().isEmpty() && orderEntity.getCost() != null;
    }
}
