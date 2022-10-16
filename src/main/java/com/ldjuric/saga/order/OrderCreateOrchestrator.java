package com.ldjuric.saga.order;

import com.ldjuric.saga.interfaces.AccountingServiceInterface;
import com.ldjuric.saga.interfaces.LogServiceInterface;
import com.ldjuric.saga.interfaces.UserServiceInterface;
import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Profile({"order", "all"})
@RequiredArgsConstructor
public class OrderCreateOrchestrator {

    private final LogServiceInterface logInterface;

    private final UserServiceInterface userInterface;

    private final WarehouseServiceInterface warehouseInterface;

    private final AccountingServiceInterface accountingInterface;

    @Autowired
    private OrderRepository orderRepository;

    public void startOrchestration(Integer orderType, String username, String password) {
        logInterface.log("[OrderCreateOrchestrator::startOrchestration] start; orderType:" + orderType);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderType(orderType);
        orderEntity.setStatus(OrderStatusEnum.INITIALIZING);
        orderRepository.save(orderEntity);
        Integer orderID = orderEntity.getId();
        logInterface.log("[OrderCreateOrchestrator::startOrchestration] created order; orderID:" + orderID);
        userInterface.validateUserOrchestration(orderID, username, password);
        warehouseInterface.createOrderOrchestration(orderID, orderType);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void userValidated(int orderID, String username, boolean validated) {
        logInterface.log("[OrderCreateOrchestrator::userValidated] start; orderID:" + orderID);
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            logInterface.log("[OrderCreateOrchestrator::userValidated] order deleted, do nothing; orderID:" + orderID);
            return;
        }

        if (!validated) {
            logInterface.log("[OrderCreateOrchestrator::userValidated] user invalid, reject order; orderID:" + orderID);
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            warehouseInterface.validateOrder(orderID, orderEntity.get().getOrderType(), false);
            return;
        }

        orderEntity.get().setUsername(username);
        orderRepository.save(orderEntity.get());
        logInterface.log("[OrderCreateOrchestrator::userValidated] order updated; orderID:" + orderID);

        if (this.validateUserAndWarehouse(orderEntity.get())) {
            logInterface.log("[OrderCreateOrchestrator::userValidated] order validated; orderID:" + orderID);
            accountingInterface.validateOrderOrchestration(orderID, orderEntity.get().getOrderType(), username, orderEntity.get().getCost());
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void warehouseValidated(int orderID, int cost, boolean validated) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            logInterface.log("[OrderCreateOrchestrator::warehouseValidated] order deleted, do nothing; orderID:" + orderID);
            return;
        }

        if (!validated) {
            logInterface.log("[OrderCreateOrchestrator::warehouseValidated] warehouse reservation invalid, reject order; orderID:" + orderID);
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setCost(cost);
        orderRepository.save(orderEntity.get());
        logInterface.log("[OrderCreateOrchestrator::warehouseValidated] order updated; orderID:" + orderID);

        if (this.validateUserAndWarehouse(orderEntity.get())) {
            logInterface.log("[OrderCreateOrchestrator::warehouseValidated] order validated; orderID:" + orderID);
            accountingInterface.validateOrderOrchestration(orderID, orderEntity.get().getOrderType(), orderEntity.get().getUsername(), cost);
        }
    }

    public void accountingValidated(int orderID, int accountingTransactionID, boolean validated) {
        logInterface.log("[OrderCreateOrchestrator::accountingValidated] start; orderID:" + orderID);
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            logInterface.log("[OrderCreateOrchestrator::accountingValidated] order deleted, do nothing; orderID:" + orderID);
            return;
        }

        if (!validated) {
            logInterface.log("[OrderCreateOrchestrator::accountingValidated] accounting transaction invalid, reject order; orderID:" + orderID);
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        logInterface.log("[OrderCreateOrchestrator::accountingValidated] order validated; orderID:" + orderID);
        orderEntity.get().setAccountingTransactionID(accountingTransactionID);
        orderEntity.get().setStatus(OrderStatusEnum.FINALIZED);
        orderRepository.save(orderEntity.get());
        logInterface.log("[OrderCreateOrchestrator::accountingValidated] order finalized; orderID:" + orderID);

        warehouseInterface.validateOrder(orderID, orderEntity.get().getOrderType(), true);
    }

    private boolean validateUserAndWarehouse(OrderEntity orderEntity) {
        return orderEntity.getUsername() != null && !orderEntity.getUsername().isEmpty() && orderEntity.getCost() != null;
    }
}
