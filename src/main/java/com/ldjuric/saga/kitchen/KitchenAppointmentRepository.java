package com.ldjuric.saga.kitchen;

import com.ldjuric.saga.user.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KitchenAppointmentRepository extends CrudRepository<KitchenAppointmentEntity, Integer> {
    @Query("select ka from KitchenAppointmentEntity ka WHERE ka.orderType = :orderType")
    List<KitchenAppointmentEntity> findAllByOrderType(@Param("orderType") Integer orderType);

    @Query("select ka from KitchenAppointmentEntity ka WHERE ka.orderID = :orderID")
    List<KitchenAppointmentEntity> findAllByOrderID(@Param("orderID") Integer orderID);
}