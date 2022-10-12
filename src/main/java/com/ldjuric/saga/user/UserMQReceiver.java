package com.ldjuric.saga.user;

import com.ldjuric.saga.interfaces.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@RabbitListener(queues = "user_input")
public class UserMQReceiver {
    @Autowired
    private UserServiceInterface userService;

    @Autowired
    private UserMQSender userSender;

    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [user service] Received '" + in + "'");
        JSONObject jsonObject = new JSONObject(in);
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        boolean result = userService.validateUser(username, password);
        userSender.send(result);
    }
}