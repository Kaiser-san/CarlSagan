package com.ldjuric.saga.warehouse;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface WarehouseStockVersionFileRepository extends CrudRepository<WarehouseStockVersionFileEntity, Integer> {
    @Query("select wr from WarehouseStockVersionFileEntity wr WHERE wr.orderID = :orderID")
    @Transactional(readOnly = true)
    Optional<WarehouseStockVersionFileEntity> findByOrderID(@Param("orderID") Integer orderID);
}
