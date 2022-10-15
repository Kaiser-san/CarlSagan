package com.ldjuric.saga.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Profile({"warehouse", "all"})
@Service
public class WarehouseService {
    @Autowired
    private WarehouseStockRepository warehouseStockRepository;

    @Autowired
    private WarehouseStockVersionFileRepository warehouseStockVersionFileRepository;

    @Autowired
    private WarehouseMQSender sender;

    public String getWarehouse(Integer id) {
        Optional<WarehouseStockEntity> warehouse = warehouseStockRepository.findById(id);
        return warehouse.isPresent() ? warehouse.get().toString() : "";
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrderOrchestration(int orderID, int orderType) {
        sender.log("[WarehouseService::createReservation] start; orderID:" + orderID);
        Optional<WarehouseStockVersionFileEntity> existingVersionFile = warehouseStockVersionFileRepository.findByOrderID(orderID);
        if (existingVersionFile.isPresent() && existingVersionFile.get().getStatus() == WarehouseStockStatusEnum.REJECTED) {
            sender.log("[WarehouseService::createReservation] already rejected, do nothing; orderID:" + orderID);
            return;
        }

        Optional<WarehouseStockEntity> warehouseStockEntity = warehouseStockRepository.findByOrderType(orderType);
        if (warehouseStockEntity.isPresent() && warehouseStockEntity.get().getStock() > 0) {
            warehouseStockEntity.get().setStock(warehouseStockEntity.get().getStock() - 1);
            warehouseStockRepository.save(warehouseStockEntity.get());

            WarehouseStockVersionFileEntity versionFile = new WarehouseStockVersionFileEntity();
            versionFile.setOrderID(orderID);
            versionFile.setOrderType(orderType);
            versionFile.setStatus(WarehouseStockStatusEnum.INITIALIZING);

            sender.log("[WarehouseService::createReservation] stock found and reduced, validate; orderID:" + orderID);
            sender.sendSuccessOrchestration(orderID, warehouseStockEntity.get().getCost());
            return;
        }
        sender.log("[WarehouseService::createReservation] no stock found for order type, reject; orderID:" + orderID);
        sender.sendFailureOrchestration(orderID);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrderChoreography(int orderID, int orderType) {
        sender.log("[WarehouseService::createReservation] start; orderID:" + orderID);
        Optional<WarehouseStockVersionFileEntity> existingVersionFile = warehouseStockVersionFileRepository.findByOrderID(orderID);
        if (existingVersionFile.isPresent() && existingVersionFile.get().getStatus() == WarehouseStockStatusEnum.REJECTED) {
            sender.log("[WarehouseService::createReservation] already rejected, do nothing; orderID:" + orderID);
            return;
        }

        Optional<WarehouseStockEntity> warehouseStockEntity = warehouseStockRepository.findByOrderType(orderType);
        if (warehouseStockEntity.isPresent() && warehouseStockEntity.get().getStock() > 0) {
            warehouseStockEntity.get().setStock(warehouseStockEntity.get().getStock() - 1);
            warehouseStockRepository.save(warehouseStockEntity.get());

            WarehouseStockVersionFileEntity versionFile = new WarehouseStockVersionFileEntity();
            versionFile.setOrderID(orderID);
            versionFile.setOrderType(orderType);
            versionFile.setStatus(WarehouseStockStatusEnum.INITIALIZING);

            sender.log("[WarehouseService::createReservation] stock found and reduced, validate; orderID:" + orderID);
            sender.sendSuccessChoreography(orderID, warehouseStockEntity.get().getCost());
            return;
        }
        sender.log("[WarehouseService::createReservation] no stock found for order type, reject; orderID:" + orderID);
        sender.sendFailureChoreography(orderID);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void validateReservation(int orderID, boolean validated) {
        sender.log("[WarehouseService::validateReservation] start; orderID:" + orderID);
        Optional<WarehouseStockVersionFileEntity> existingVersionFile = warehouseStockVersionFileRepository.findByOrderID(orderID);
        if (existingVersionFile.isPresent()) {
            if (validated) {
                sender.log("[WarehouseService::validateReservation] validated reservation, finalize it; orderID:" + orderID);
                existingVersionFile.get().setStatus(WarehouseStockStatusEnum.FINALIZED);
                warehouseStockVersionFileRepository.save(existingVersionFile.get());
            }
            else {
                sender.log("[WarehouseService::validateReservation] accounting invalid, reject; orderID:" + orderID);
                existingVersionFile.get().setStatus(WarehouseStockStatusEnum.REJECTED);
                warehouseStockVersionFileRepository.save(existingVersionFile.get());

                Optional<WarehouseStockEntity> warehouseStockEntity = warehouseStockRepository.findByOrderType(existingVersionFile.get().getOrderType());
                warehouseStockEntity.get().setStock(warehouseStockEntity.get().getStock() + 1);
                warehouseStockRepository.save(warehouseStockEntity.get());
                sender.log("[WarehouseService::validateReservation] accounting invalid, rejected and saved; orderID:" + orderID);
            }
        }
        else {
            //if not present, add a version file entity, so that when the message from order comes, we can reject it
            //should only return invalid here, as accounting shouldn't finalize without our message first
            WarehouseStockVersionFileEntity versionFile = new WarehouseStockVersionFileEntity();
            versionFile.setOrderID(orderID);
            versionFile.setStatus(validated ? WarehouseStockStatusEnum.FINALIZED : WarehouseStockStatusEnum.REJECTED);
            warehouseStockVersionFileRepository.save(versionFile);
            sender.log("[WarehouseService::createReservation] create version file; orderID:" + orderID + " status:" + versionFile.getStatus());
        }
    }
}
