package com.ldjuric.saga.warehouse;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseReservationRepository extends CrudRepository<WarehouseReservationEntity, Integer> {
    @Query("select wr from WarehouseReservationEntity wr WHERE wr.orderType = :orderType")
    @Transactional(readOnly = true)
    List<WarehouseReservationEntity> findAllByOrderType(@Param("orderType") Integer orderType);

    @Query("select wr from WarehouseReservationEntity wr WHERE wr.orderID = :orderID")
    @Transactional(readOnly = true)
    Optional<WarehouseReservationEntity> findByOrderID(@Param("orderID") Integer orderID);
}