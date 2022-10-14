package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.accounting.AccountingTransactionEntity;
import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import com.ldjuric.saga.order.OrderMQSender;
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

    @Autowired
    private WarehouseMQSender sender;

    public String getWarehouseName(Integer id) {
        Optional<WarehouseEntity> warehouse = warehouseRepository.findById(id);
        return warehouse.isPresent() ? warehouse.get().getName() : "";
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WarehouseReservationStatusEnum createReservation(int orderID, int orderType) {
        sender.log("[WarehouseService::createReservation] start; orderID:" + orderID);
        Optional<WarehouseReservationVersionFileEntity> versionFile = warehouseReservationVersionFileRepository.findByOrderID(orderID);
        if (versionFile.isPresent() && versionFile.get().getStatus() == WarehouseReservationStatusEnum.REJECTED) {
            sender.log("[WarehouseService::createReservation] already rejected, do nothing; orderID:" + orderID);
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
            sender.log("[WarehouseService::createReservation] free warehouse found, validate; orderID:" + orderID);
            return WarehouseReservationStatusEnum.FINALIZED;
        }
        sender.log("[WarehouseService::createReservation] no free warehouses, reject; orderID:" + orderID);
        return WarehouseReservationStatusEnum.REJECTED;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void validateReservation(int orderID, boolean validated) {
        sender.log("[WarehouseService::validateReservation] start; orderID:" + orderID);
        Optional<WarehouseReservationEntity> warehouseReservation = warehouseReservationRepository.findByOrderID(orderID);
        if (warehouseReservation.isPresent()) {
            if (validated) {
                sender.log("[WarehouseService::createReservation] validateReservation, finalize it; orderID:" + orderID);
                warehouseReservation.get().setStatus(WarehouseReservationStatusEnum.FINALIZED);
                warehouseReservationRepository.save(warehouseReservation.get());
            }
            else {
                sender.log("[WarehouseService::createReservation] accounting invalid, reject; orderID:" + orderID);
                warehouseReservationRepository.delete(warehouseReservation.get());
            }
        }
        else {
            if (warehouseReservationVersionFileRepository.findByOrderID(orderID).isPresent()) {
                sender.log("[WarehouseService::createReservation] version file already present; orderID:" + orderID);
                return;
            }
            //if not present, add a version file entity, so that when the message from order comes, we can reject it
            //should only return invalid here, as accounting shouldn't finalize without our message first
            WarehouseReservationVersionFileEntity versionFile = new WarehouseReservationVersionFileEntity();
            versionFile.setOrderID(orderID);
            versionFile.setStatus(validated ? WarehouseReservationStatusEnum.FINALIZED : WarehouseReservationStatusEnum.REJECTED);
            warehouseReservationVersionFileRepository.save(versionFile);
            sender.log("[WarehouseService::createReservation] create version file; orderID:" + orderID + " status:" + versionFile.getStatus());
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WarehouseReservationEntity getWarehouseReservation(int orderID) {
        Optional<WarehouseReservationEntity> reservation = warehouseReservationRepository.findByOrderID(orderID);
        return reservation.orElse(null);
    }
}
