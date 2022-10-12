package com.ldjuric.saga.order;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class OrderCreateOrchestrator {

    @Autowired
    private OrderMQSender orderSender;

    @Autowired
    private OrderRepository orderRepository;

    public void startOrchestration(Integer orderType, String username, String password) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderType(orderType);
        orderEntity.setStatus(OrderStatusEnum.INITIALIZING);
        orderRepository.save(orderEntity);
        Integer orderID = orderEntity.getId();
        orderSender.sendUserValidate(orderID, username, password);
        orderSender.sendKitchenValidate(orderID, orderType);
    }

    public void userValidated(int orderID, String username, boolean validated) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            return;
        }

        if (!validated) {
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setUsername(username);
        orderRepository.save(orderEntity.get());

        if (this.validateUserAndKitchen(orderEntity.get())) {
            orderSender.sendAccountingValidate(orderEntity.get());
        }
    }

    public void kitchenValidated(int orderID, int kitchenAppointmentID, int cost, boolean validated) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            return;
        }

        if (!validated) {
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setKitchen_appointments_id(kitchenAppointmentID);
        orderEntity.get().setCost(cost);
        orderRepository.save(orderEntity.get());

        if (this.validateUserAndKitchen(orderEntity.get())) {
            orderSender.sendAccountingValidate(orderEntity.get());
        }
    }

    public void accountingValidated(int orderID, int accountingTransactionID, boolean validated) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            return;
        }

        if (!validated) {
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setAccounting_transactions_id(accountingTransactionID);
        orderEntity.get().setStatus(OrderStatusEnum.FINALIZED);
        orderRepository.save(orderEntity.get());
    }

    private boolean validateUserAndKitchen(OrderEntity orderEntity) {
        return !orderEntity.getUsername().isEmpty() && orderEntity.getKitchen_appointments_id() != 0;
    }
}
