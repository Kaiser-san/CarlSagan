package com.ldjuric.saga.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public String getOrder(Integer id) {
        Optional<OrderEntity> order = orderRepository.findById(id);
        return order.isPresent() ? order.get().toString() : "";
    }

    public void orchestrationCreate(Long orderType, String username, String password) {

    }

    public void choreographyCreate(Long orderType, String username, String password) {

    }
}
