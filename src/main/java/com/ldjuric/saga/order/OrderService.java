package com.ldjuric.saga.order;

import com.ldjuric.saga.logs.interfaces.OrderServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class OrderService implements OrderServiceInterface {

    @Autowired
    private OrderCreateOrchestrator orderCreateOrchestrator;

    @Autowired
    private OrderRepository orderRepository;

    public String getOrder(Integer orderID) {
        Optional<OrderEntity> order = orderRepository.findById(orderID);
        return order.isPresent() ? order.get().toString() : "";
    }

    public void orchestrationCreate(Integer orderType, String username, String password) {
        orderCreateOrchestrator.startOrchestration(orderType, username, password);
    }

    public Integer choreographyCreate(Integer orderType, String username, String password) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderType(orderType);
        orderEntity.setUsername(username);
        orderEntity.setStatus(OrderStatusEnum.INITIALIZING);
        orderRepository.save(orderEntity);
        return orderEntity.getId();
    }

    @Override
    public void accountingValidatedChoreography(int orderID, String username, int kitchenAppointmentID, int cost, int accountingTransactionID, boolean validated) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(orderID);
        if (orderEntity.isEmpty()) {
            return;
        }

        if (!validated) {
            orderEntity.get().setStatus(OrderStatusEnum.REJECTED);
            return;
        }

        orderEntity.get().setUsername(username);
        orderEntity.get().setKitchen_appointments_id(kitchenAppointmentID);
        orderEntity.get().setCost(cost);
        orderEntity.get().setAccounting_transactions_id(accountingTransactionID);
        orderEntity.get().setStatus(OrderStatusEnum.FINALIZED);
        orderRepository.save(orderEntity.get());
    }
}
