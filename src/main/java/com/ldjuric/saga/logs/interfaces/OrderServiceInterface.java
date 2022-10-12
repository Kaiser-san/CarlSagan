package com.ldjuric.saga.logs.interfaces;

public interface OrderServiceInterface {
    String getOrder(Integer id);

    void orchestrationCreate(Integer orderType, String username, String password);

    Integer choreographyCreate(Integer orderType, String username, String password);

    void accountingValidatedChoreography(int orderID, String username, int kitchenAppointmentID, int cost, int accountingTransactionID, boolean validated);
}
