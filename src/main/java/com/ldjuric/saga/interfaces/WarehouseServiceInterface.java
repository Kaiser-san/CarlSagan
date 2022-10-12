package com.ldjuric.saga.interfaces;

import com.ldjuric.saga.warehouse.WarehouseReservationEntity;

import java.util.Optional;

public interface WarehouseServiceInterface {
    String getWarehouseName(Integer id);

    Optional<WarehouseReservationEntity> createReservation(int orderID, int orderType);

    void validateReservation(int orderID, boolean validated);
}
