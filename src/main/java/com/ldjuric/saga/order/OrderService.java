package com.ldjuric.saga.order;

import com.ldjuric.saga.interfaces.OrderServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class OrderService implements OrderServiceInterface {

    @Autowired
    private OrderCreateOrchestrator orderCreateOrchestrator;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMQSender sender;

    public String getOrder(Integer orderID) {
        Optional<OrderEntity> order = orderRepository.findById(orderID);
        return order.isPresent() ? order.get().toString() : "";
    }

    @Transactional
    public void orchestrationCreate(Integer orderType, String username, String password) {
        sender.log("[OrderService::orchestrationCreate] start; orderType:" + orderType);
        orderCreateOrchestrator.startOrchestration(orderType, username, password);
    }

    @Transactional
    public Integer choreographyCreate(Integer orderType, String username, String password) {
        sender.log("[OrderService::choreographyCreate] start; orderType:" + orderType);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderType(orderType);
        orderEntity.setUsername(username);
        orderEntity.setStatus(OrderStatusEnum.INITIALIZING);
        orderRepository.save(orderEntity);
        return orderEntity.getId();
    }

    @Override
    @Transactional
    public void accountingValidatedChoreography(int orderID, String username, int warehouseReservationID, int cost, int accountingTransactionID, boolean validated) {
        sender.log("[OrderService::accountingValidatedChoreography] start; orderID:" + orderID);
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            sender.log("[OrderService::accountingValidatedChoreography] order deleted, do nothing; orderID:" + orderID);
            return;
        }

        if (!validated) {
            sender.log("[OrderService::accountingValidatedChoreography] order invalid, reject it; orderID:" + orderID);
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setUsername(username);
        orderEntity.get().setWarehouseReservationID(warehouseReservationID);
        orderEntity.get().setCost(cost);
        orderEntity.get().setAccountingTransactionID(accountingTransactionID);
        orderEntity.get().setStatus(OrderStatusEnum.FINALIZED);
        orderRepository.save(orderEntity.get());
        sender.log("[OrderService::accountingValidatedChoreography] order valid saved; orderID:" + orderID);
    }
}
