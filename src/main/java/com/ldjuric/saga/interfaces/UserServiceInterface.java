package com.ldjuric.saga.interfaces;

public interface UserServiceInterface {
    boolean validateUser(String username, String password);

    String getUser(String username);
}
