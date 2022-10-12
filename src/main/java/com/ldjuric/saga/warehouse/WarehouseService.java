package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WarehouseService implements WarehouseServiceInterface {
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private WarehouseReservationRepository warehouseReservationRepository;

    public String getWarehouseName(Integer id) {
        Optional<WarehouseEntity> warehouse = warehouseRepository.findById(id);
        return warehouse.isPresent() ? warehouse.get().getName() : "";
    }

    @Override
    @Transactional
    public Optional<WarehouseReservationEntity> createReservation(int orderID, int orderType) {
        List<WarehouseReservationEntity> warehouseReservations = warehouseReservationRepository.findAllByOrderType(orderType);
        Set<WarehouseEntity> warehouseEntities = new HashSet<WarehouseEntity>((Collection) warehouseRepository.findAll());
        for (WarehouseReservationEntity warehouseReservation : warehouseReservations) {
            if (warehouseReservation.getStatus() == WarehouseReservationStatusEnum.INITIALIZING || warehouseReservation.getStatus() == WarehouseReservationStatusEnum.FINALIZED) {
                //filter out approved or in progress appointments
                warehouseEntities.remove(warehouseReservation.getWarehouse());
            }
        }
        Optional<WarehouseEntity> warehouseEntity = warehouseEntities.stream().findAny();
        //if, after filtering, there are still available warehouses, pick any
        if (warehouseEntity.isPresent()) {
            WarehouseReservationEntity warehouseReservation = new WarehouseReservationEntity();
            warehouseReservation.setWarehouse(warehouseEntity.get());
            warehouseReservation.setOrderID(orderID);
            warehouseReservation.setOrderType(orderType);
            warehouseReservation.setStatus(WarehouseReservationStatusEnum.INITIALIZING);
            warehouseReservationRepository.save(warehouseReservation);
            return Optional.of(warehouseReservation);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void validateReservation(int orderID, boolean validated) {
        List<WarehouseReservationEntity> warehouseReservations = warehouseReservationRepository.findAllByOrderID(orderID);
        for (WarehouseReservationEntity warehouseReservation : warehouseReservations) {
            //filter out rejected appointments
            if (warehouseReservation.getStatus() == WarehouseReservationStatusEnum.INITIALIZING) {
                warehouseReservation.setStatus(validated ? WarehouseReservationStatusEnum.FINALIZED : WarehouseReservationStatusEnum.REJECTED);
                //should only be one in progress appointment per order (can have rejected orders)
                return;
            }
        }
    }
}
