package com.ldjuric.saga.warehouse;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Profile({"warehouse", "all"})
@Repository
public interface WarehouseStockRepository extends CrudRepository<WarehouseStockEntity, Integer> {
    Optional<WarehouseStockEntity> findByOrderType(int orderType);
}
