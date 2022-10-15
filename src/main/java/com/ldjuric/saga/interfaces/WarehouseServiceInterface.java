package com.ldjuric.saga.interfaces;

import com.ldjuric.saga.warehouse.WarehouseStockStatusEnum;

public interface WarehouseServiceInterface {
    WarehouseStockStatusEnum createOrder(int orderID, int orderType);

    void validateReservation(int orderID, boolean validated);
}
