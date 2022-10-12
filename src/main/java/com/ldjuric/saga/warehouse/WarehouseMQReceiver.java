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

    @RabbitListener(queues = {"warehouse_input", "order_output"})
    public void receiveCreateAppointment(String in) {
        System.out.println(" [warehouse service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        int orderType = jsonObject.getInt("orderType");
        Optional<WarehouseReservationEntity> warehouseReservation = warehouseService.createReservation(orderID, orderType);
        if (warehouseReservation.isPresent()) {
            warehouseSender.sendSuccess(orderID, warehouseReservation.get().getId(), warehouseReservation.get().getWarehouse().getCost());
        }
        else {
            warehouseSender.sendFailure(orderID);
        }
    }

    @RabbitListener(queues = "accounting_output")
    public void receiveValidateAppointment(String in) {
        System.out.println(" [warehouse service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        boolean validated = jsonObject.getBoolean("validated");
        warehouseService.validateReservation(orderID, validated);
    }
}
