package com.ldjuric.saga.logs.interfaces;

public interface UserServiceInterface {
    boolean validateUser(String username, String password);

    String getUser(String username);
}
