package com.ldjuric.saga.kitchen;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenRepository extends CrudRepository<KitchenEntity, Integer> {
}
