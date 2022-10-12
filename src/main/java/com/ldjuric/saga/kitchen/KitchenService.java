package com.ldjuric.saga.kitchen;

import com.ldjuric.saga.interfaces.KitchenServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class KitchenService implements KitchenServiceInterface {
    @Autowired
    private KitchenRepository kitchenRepository;
    @Autowired
    private KitchenAppointmentRepository kitchenAppointmentRepository;

    public String getKitchenName(Integer id) {
        Optional<KitchenEntity> kitchen = kitchenRepository.findById(id);
        return kitchen.isPresent() ? kitchen.get().getName() : "";
    }

    @Override
    @Transactional
    public Optional<KitchenAppointmentEntity> createAppointment(int orderID, int orderType) {
        List<KitchenAppointmentEntity> kitchenAppointments = kitchenAppointmentRepository.findAllByOrderType(orderType);
        Set<KitchenEntity> kitchenEntities = new HashSet<KitchenEntity>((Collection)kitchenRepository.findAll());
        for (KitchenAppointmentEntity kitchenAppointment : kitchenAppointments) {
            if (kitchenAppointment.getStatus() == KitchenAppointmentStatusEnum.INITIALIZING || kitchenAppointment.getStatus() == KitchenAppointmentStatusEnum.FINALIZED) {
                //filter out approved or in progress appointments
                kitchenEntities.remove(kitchenAppointment.getKitchen());
            }
        }
        Optional<KitchenEntity> kitchenEntity = kitchenEntities.stream().findAny();
        //if, after filtering, there are still available kitchens, pick any
        if (kitchenEntity.isPresent()) {
            KitchenAppointmentEntity kitchenAppointment = new KitchenAppointmentEntity();
            kitchenAppointment.setKitchen(kitchenEntity.get());
            kitchenAppointment.setOrderID(orderID);
            kitchenAppointment.setOrderType(orderType);
            kitchenAppointment.setStatus(KitchenAppointmentStatusEnum.INITIALIZING);
            kitchenAppointmentRepository.save(kitchenAppointment);
            return Optional.of(kitchenAppointment);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void validateAppointment(int orderID, boolean validated) {
        List<KitchenAppointmentEntity> kitchenAppointments = kitchenAppointmentRepository.findAllByOrderID(orderID);
        for (KitchenAppointmentEntity kitchenAppointment : kitchenAppointments) {
            //filter out rejected appointments
            if (kitchenAppointment.getStatus() == KitchenAppointmentStatusEnum.INITIALIZING) {
                kitchenAppointment.setStatus(validated ? KitchenAppointmentStatusEnum.FINALIZED : KitchenAppointmentStatusEnum.REJECTED);
                //should only be one in progress appointment per order (can have rejected orders)
                return;
            }
        }
    }
}
