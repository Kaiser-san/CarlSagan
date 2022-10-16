package com.ldjuric.saga.logs;


import com.ldjuric.saga.interfaces.LogServiceInterface;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Profile({"log", "all"})
@Service
public class LogService implements LogServiceInterface {
    private ArrayList<String> logs;

    public LogService() {
        logs = new ArrayList<>();
    }

    public void log(String log) {
        logs.add(log);
    }

    public String getLogs() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String log : logs) {
            stringBuilder.append(log);
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }
}
