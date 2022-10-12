package com.ldjuric.saga.warehouse;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends CrudRepository<WarehouseEntity, Integer> {
}
