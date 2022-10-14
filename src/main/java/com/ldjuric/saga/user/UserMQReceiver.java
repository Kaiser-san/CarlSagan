package com.ldjuric.saga.user;

import com.ldjuric.saga.interfaces.UserServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMQReceiver {
    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private UserMQSender userSender;

    @RabbitListener(queues = "user_input")
    public void receiveOrchestration(String in) {
        System.out.println(" [user service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        boolean result = userService.validateUser(username, password);
        userSender.sendOrchestration(result, orderID, username);
    }

    @RabbitListener(queues = "#{userOrderOutputQueue.name}")
    public void receiveChoreography(String in) {
        System.out.println(" [user service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        boolean result = userService.validateUser(username, password);
        userSender.sendChoreography(result, orderID, username);
    }
}
