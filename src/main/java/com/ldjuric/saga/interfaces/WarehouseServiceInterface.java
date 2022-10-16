package com.ldjuric.saga.interfaces;

import com.ldjuric.saga.warehouse.WarehouseStockStatusEnum;

public interface WarehouseServiceInterface {
    void createOrderOrchestration(Integer orderID, Integer orderType);

    void validateOrder(Integer orderID, Integer orderType, boolean validated);
}
