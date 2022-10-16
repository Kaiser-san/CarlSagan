package com.ldjuric.saga.interfaces;

public interface UserServiceInterface {
    void validateUserOrchestration(Integer orderID, String username, String password);
}
