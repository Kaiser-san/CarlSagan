package com.ldjuric.saga.kitchen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KitchenService {
    private final KitchenRepository kitchenRepository;
    private final KitchenAppointmentRepository kitchenAppointmentRepository;

    public String getKitchenName(Integer id) {
        Optional<KitchenEntity> kitchen = kitchenRepository.findById(id);
        return kitchen.isPresent() ? kitchen.get().getName() : "";
    }
}
