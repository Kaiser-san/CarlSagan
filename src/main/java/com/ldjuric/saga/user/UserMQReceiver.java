package com.ldjuric.saga.user;

import com.ldjuric.saga.interfaces.UserServiceInterface;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

@Profile({"user", "all"})
public class UserMQReceiver {
    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private UserMQSender sender;

    @RabbitListener(queues = "user_input")
    public void receiveOrchestration(String in) {
        sender.log("[UserService::receiveOrchestration] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        boolean result = userService.validateUser(username, password);
        sender.sendOrchestration(result, orderID, username);
    }

    @RabbitListener(queues = "#{userOrderOutputQueue.name}")
    public void receiveChoreography(String in) {
        sender.log("[UserService::receiveChoreography] '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        int orderID = jsonObject.getInt("orderID");
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        boolean result = userService.validateUser(username, password);
        sender.sendChoreography(result, orderID, username);
    }
}
