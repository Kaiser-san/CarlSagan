package com.ldjuric.saga.kitchen;

import com.ldjuric.saga.logs.interfaces.KitchenServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class KitchenMQReceiver {
    @Autowired
    private KitchenServiceInterface kitchenService;

    @Autowired
    private KitchenMQSender kitchenSender;

    @RabbitListener(queues = {"kitchen_input", "order_output"})
    public void receiveCreateAppointment(String in) {
        System.out.println(" [kitchen service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        int orderType = jsonObject.getInt("order_type");
        Optional<KitchenAppointmentEntity> kitchenAppointment = kitchenService.createAppointment(orderID, orderType);
        if (kitchenAppointment.isPresent()) {
            kitchenSender.sendSuccess(orderID, kitchenAppointment.get().getId(), kitchenAppointment.get().getKitchen().getCost());
        }
        else {
            kitchenSender.sendFailure(orderID);
        }
    }

    @RabbitListener(queues = "accounting_output")
    public void receiveValidateAppointment(String in) {
        System.out.println(" [kitchen service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("order_id");
        boolean validated = jsonObject.getBoolean("validated");
        kitchenService.validateAppointment(orderID, validated);
    }
}
