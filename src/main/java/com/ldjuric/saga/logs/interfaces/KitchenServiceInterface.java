package com.ldjuric.saga.logs.interfaces;

import com.ldjuric.saga.kitchen.KitchenAppointmentEntity;

import java.util.Optional;

public interface KitchenServiceInterface {
    String getKitchenName(Integer id);

    Optional<KitchenAppointmentEntity> createAppointment(int orderID, int orderType);

    void validateAppointment(int orderID, boolean validated);
}
