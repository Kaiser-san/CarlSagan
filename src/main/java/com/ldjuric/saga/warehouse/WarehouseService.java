package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.accounting.AccountingTransactionEntity;
import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WarehouseService implements WarehouseServiceInterface {
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private WarehouseReservationRepository warehouseReservationRepository;

    @Autowired
    private WarehouseReservationVersionFileRepository warehouseReservationVersionFileRepository;

    public String getWarehouseName(Integer id) {
        Optional<WarehouseEntity> warehouse = warehouseRepository.findById(id);
        return warehouse.isPresent() ? warehouse.get().getName() : "";
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WarehouseReservationStatusEnum createReservation(int orderID, int orderType) {
        Optional<WarehouseReservationVersionFileEntity> versionFile = warehouseReservationVersionFileRepository.findByOrderID(orderID);
        if (versionFile.isPresent() && versionFile.get().getStatus() == WarehouseReservationStatusEnum.REJECTED) {
            //if a version file exists, it means accounting already rejected the transaction so don't do anything
            return WarehouseReservationStatusEnum.INITIALIZING;
        }

        List<WarehouseReservationEntity> warehouseReservations = warehouseReservationRepository.findAllByOrderType(orderType);
        Set<WarehouseEntity> warehouseEntities = new HashSet<WarehouseEntity>((Collection) warehouseRepository.findAll());
        for (WarehouseReservationEntity warehouseReservation : warehouseReservations) {
            if (warehouseReservation.getStatus() == WarehouseReservationStatusEnum.INITIALIZING || warehouseReservation.getStatus() == WarehouseReservationStatusEnum.FINALIZED) {
                //filter out approved or in progress reservations
                warehouseEntities.remove(warehouseReservation.getWarehouse());
            }
        }

        Optional<WarehouseEntity> warehouseEntity = warehouseEntities.stream().min(Comparator.comparingInt(WarehouseEntity::getCost));
        //if, after filtering, there are still available warehouses, pick the lowest cost one
        if (warehouseEntity.isPresent()) {
            WarehouseReservationEntity warehouseReservation = new WarehouseReservationEntity();
            warehouseReservation.setWarehouse(warehouseEntity.get());
            warehouseReservation.setOrderID(orderID);
            warehouseReservation.setOrderType(orderType);
            warehouseReservation.setStatus(WarehouseReservationStatusEnum.INITIALIZING);
            warehouseReservationRepository.save(warehouseReservation);
            return WarehouseReservationStatusEnum.FINALIZED;
        }
        return WarehouseReservationStatusEnum.REJECTED;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void validateReservation(int orderID, boolean validated) {
        Optional<WarehouseReservationEntity> warehouseReservation = warehouseReservationRepository.findByOrderID(orderID);
        if (warehouseReservation.isPresent()) {
            if (validated) {
                //if present and accounting returned valid result, finalize it
                warehouseReservation.get().setStatus(WarehouseReservationStatusEnum.FINALIZED);
                warehouseReservationRepository.save(warehouseReservation.get());
            }
            else {
                //if present and accounting returned invalid result, delete it
                warehouseReservationRepository.delete(warehouseReservation.get());
            }
        }
        else {
            if (warehouseReservationVersionFileRepository.findByOrderID(orderID).isPresent()) {
                return;
            }
            //if not present, add a version file entity, so that when the message from order comes, we can reject it
            //should only return invalid here, as accounting shouldn't finalize without our message first
            WarehouseReservationVersionFileEntity versionFile = new WarehouseReservationVersionFileEntity();
            versionFile.setOrderID(orderID);
            versionFile.setStatus(validated ? WarehouseReservationStatusEnum.FINALIZED : WarehouseReservationStatusEnum.REJECTED);
            warehouseReservationVersionFileRepository.save(versionFile);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WarehouseReservationEntity getWarehouseReservation(int orderID) {
        Optional<WarehouseReservationEntity> reservation = warehouseReservationRepository.findByOrderID(orderID);
        return reservation.orElse(null);
    }
}
