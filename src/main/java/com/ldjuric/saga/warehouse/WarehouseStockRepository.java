package com.ldjuric.saga.warehouse;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends CrudRepository<WarehouseStockEntity, Integer> {
    Optional<WarehouseStockEntity> findByOrderType(int orderType);
}
