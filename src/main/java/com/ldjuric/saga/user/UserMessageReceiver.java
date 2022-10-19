package com.ldjuric.saga.user;

import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

@Profile({"user", "all"})
public class UserMessageReceiver {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMessageSender sender;

    @RabbitListener(queues = "user_input_orchestration")
    public void receiveOrchestration(String in) {
        sender.log("[UserService::receiveOrchestration] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        userService.validateUserOrchestration(orderID, username, password);
    }

    @RabbitListener(queues = "#{userOrderOutputQueue.name}")
    public void receiveChoreography(String in) {
        sender.log("[UserService::receiveChoreography] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        userService.validateUserChoreography(orderID, username, password);
    }
}
