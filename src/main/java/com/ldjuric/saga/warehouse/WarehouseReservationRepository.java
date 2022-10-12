package com.ldjuric.saga.warehouse;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseReservationRepository extends CrudRepository<WarehouseReservationEntity, Integer> {
    @Query("select wr from WarehouseReservationEntity wr WHERE wr.orderType = :orderType")
    List<WarehouseReservationEntity> findAllByOrderType(@Param("orderType") Integer orderType);

    @Query("select wr from WarehouseReservationEntity wr WHERE wr.orderID = :orderID")
    List<WarehouseReservationEntity> findAllByOrderID(@Param("orderID") Integer orderID);
}