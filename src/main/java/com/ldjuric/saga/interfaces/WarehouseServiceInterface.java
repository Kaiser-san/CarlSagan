package com.ldjuric.saga.interfaces;

import com.ldjuric.saga.warehouse.WarehouseReservationEntity;
import com.ldjuric.saga.warehouse.WarehouseReservationStatusEnum;

public interface WarehouseServiceInterface {
    String getWarehouseName(Integer id);

    WarehouseReservationStatusEnum createReservation(int orderID, int orderType);

    void validateReservation(int orderID, boolean validated);

    WarehouseReservationEntity getWarehouseReservation(int orderID);
}
