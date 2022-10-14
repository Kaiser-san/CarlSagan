package com.ldjuric.saga.logs;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class LogMQReceiver {
    @Autowired
    private LogService logService;

    @RabbitListener(queues = "log_input")
    public void receive(String in) {
        System.out.println("Log " + in);
        logService.addLog(in);
    }

}
