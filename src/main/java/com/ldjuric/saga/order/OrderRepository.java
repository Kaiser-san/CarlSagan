package com.ldjuric.saga.order;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Profile({"order", "all"})
@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, Integer> {
}