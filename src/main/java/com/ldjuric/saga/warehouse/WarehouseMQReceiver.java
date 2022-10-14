package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class WarehouseMQReceiver {
    @Autowired
    private WarehouseServiceInterface warehouseService;

    @Autowired
    private WarehouseMQSender sender;

    @RabbitListener(queues = "warehouse_input")
    public void receiveCreateReservationOrchestration(String in) {
        sender.log("[WarehouseService::receiveCreateReservationOrchestration] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        WarehouseReservationStatusEnum status = warehouseService.createReservation(orderID, orderType);
        if (status == WarehouseReservationStatusEnum.FINALIZED) {
            WarehouseReservationEntity reservation = warehouseService.getWarehouseReservation(orderID);
            sender.sendSuccessOrchestration(orderID, reservation.getId(), reservation.getWarehouse().getCost());
        }
        else if (status == WarehouseReservationStatusEnum.REJECTED){
            sender.sendFailureOrchestration(orderID);
        }
    }

    @RabbitListener(queues = "#{warehouseOrderOutputQueue.name}")
    public void receiveCreateReservationChoreography(String in) {
        sender.log("[WarehouseService::receiveCreateReservationChoreography] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        WarehouseReservationStatusEnum status = warehouseService.createReservation(orderID, orderType);
        if (status == WarehouseReservationStatusEnum.FINALIZED) {
            WarehouseReservationEntity reservation = warehouseService.getWarehouseReservation(orderID);
            sender.sendSuccessChoreography(orderID, reservation.getId(), reservation.getWarehouse().getCost());
        }
        else if (status == WarehouseReservationStatusEnum.REJECTED){
            sender.sendFailureChoreography(orderID);
        }
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
