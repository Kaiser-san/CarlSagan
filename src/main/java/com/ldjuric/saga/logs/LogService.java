package com.ldjuric.saga.logs;


import java.util.ArrayList;

public class LogService {
    private ArrayList<String> logs;

    public LogService() {
        logs = new ArrayList<>();
    }

    public void addLog(String log) {
        logs.add(log);
    }

    public String getLogs() {
        StringBuilder str = new StringBuilder();
        for (String log : logs) {
            str.append(log);
        }
        return str.toString();
    }
}
