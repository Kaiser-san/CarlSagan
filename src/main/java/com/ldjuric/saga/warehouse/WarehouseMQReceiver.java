package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class WarehouseMQReceiver {
    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private WarehouseMQSender sender;

    @RabbitListener(queues = "warehouse_input")
    public void receiveCreateOrderOrchestration(String in) {
        sender.log("[WarehouseService::receiveCreateReservationOrchestration] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        warehouseService.createOrderOrchestration(orderID, orderType);
    }

    @RabbitListener(queues = "warehouse_input_invalidate")
    public void receiveInvalidateOrderOrchestration(String in) {
        sender.log("[WarehouseService::receiveInvalidateOrderOrchestration] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        boolean validated = jsonObject.getBoolean("validated");
        warehouseService.validateReservation(orderID, validated);
    }

    @RabbitListener(queues = "#{warehouseOrderOutputQueue.name}")
    public void receiveCreateReservationChoreography(String in) {
        sender.log("[WarehouseService::receiveCreateReservationChoreography] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        warehouseService.createOrderChoreography(orderID, orderType);
    }

    @RabbitListener(queues = "#{warehouseAccountingOutputQueue.name}")
    public void receiveValidateAppointment(String in) {
        sender.log("[WarehouseService::receiveValidateAppointment] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        boolean validated = jsonObject.getBoolean("validated");
        warehouseService.validateReservation(orderID, validated);
    }
}
