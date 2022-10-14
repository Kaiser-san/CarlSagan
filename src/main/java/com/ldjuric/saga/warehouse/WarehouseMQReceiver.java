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
    private WarehouseMQSender warehouseSender;

    @RabbitListener(queues = "warehouse_input")
    public void receiveCreateReservationOrchestration(String in) {
        System.out.println(" [warehouse service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        WarehouseReservationStatusEnum status = warehouseService.createReservation(orderID, orderType);
        if (status == WarehouseReservationStatusEnum.FINALIZED) {
            WarehouseReservationEntity reservation = warehouseService.getWarehouseReservation(orderID);
            warehouseSender.sendSuccessOrchestration(orderID, reservation.getId(), reservation.getWarehouse().getCost());
        }
        else if (status == WarehouseReservationStatusEnum.REJECTED){
            warehouseSender.sendFailureOrchestration(orderID);
        }
    }

    @RabbitListener(queues = "#{warehouseOrderOutputQueue.name}")
    public void receiveCreateReservationChoreography(String in) {
        System.out.println(" [warehouse service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        WarehouseReservationStatusEnum status = warehouseService.createReservation(orderID, orderType);
        if (status == WarehouseReservationStatusEnum.FINALIZED) {
            WarehouseReservationEntity reservation = warehouseService.getWarehouseReservation(orderID);
            warehouseSender.sendSuccessChoreography(orderID, reservation.getId(), reservation.getWarehouse().getCost());
        }
        else if (status == WarehouseReservationStatusEnum.REJECTED){
            warehouseSender.sendFailureChoreography(orderID);
        }
    }

    @RabbitListener(queues = "#{warehouseAccountingOutputQueue.name}")
    public void receiveValidateAppointment(String in) {
        System.out.println(" [warehouse service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        boolean validated = jsonObject.getBoolean("validated");
        warehouseService.validateReservation(orderID, validated);
    }
}
