package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Profile({"warehouse", "all"})
@Service
public class WarehouseService implements WarehouseServiceInterface {
    @Autowired
    private WarehouseStockRepository warehouseStockRepository;

    @Autowired
    private WarehouseStockVersionFileRepository warehouseStockVersionFileRepository;

    @Autowired
    private WarehouseMessageSender sender;

    public String getWarehouseStock() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterable<WarehouseStockEntity> stockEntities = warehouseStockRepository.findAll();
        for (WarehouseStockEntity stockEntity : stockEntities) {
            stringBuilder.append(stockEntity.toString());
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }

    public String getWarehouseStock(Integer orderType) {
        Optional<WarehouseStockEntity> stockEntity = warehouseStockRepository.findByOrderType(orderType);
        return stockEntity.isPresent() ? stockEntity.get().toString() : "";
    }

    public String getWarehouseVersionFile() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterable<WarehouseStockVersionFileEntity> versionFileEntities = warehouseStockVersionFileRepository.findAll();
        for (WarehouseStockVersionFileEntity versionFile : versionFileEntities) {
            stringBuilder.append(versionFile.toString());
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }

    public boolean createWarehouseStock(Integer orderType, Integer cost, Integer stock) {
        Optional<WarehouseStockEntity> existingStockEntity = warehouseStockRepository.findByOrderType(orderType);
        if (existingStockEntity.isPresent()) {
            return false;
        }

        WarehouseStockEntity stockEntity = new WarehouseStockEntity();
        stockEntity.setOrderType(orderType);
        stockEntity.setCost(cost);
        stockEntity.setStock(stock);
        warehouseStockRepository.save(stockEntity);
        return true;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrderOrchestration(Integer orderID, Integer orderType) {
        sender.log("[WarehouseService::createOrderOrchestration] start; orderID:" + orderID);
        Optional<WarehouseStockVersionFileEntity> existingVersionFile = warehouseStockVersionFileRepository.findByOrderID(orderID);
        if (existingVersionFile.isPresent() && existingVersionFile.get().getStatus() == WarehouseStockStatusEnum.REJECTED) {
            sender.log("[WarehouseService::createOrderOrchestration] already rejected, do nothing; orderID:" + orderID);
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
            warehouseStockVersionFileRepository.save(versionFile);

            sender.log("[WarehouseService::createOrderOrchestration] stock found and reduced, validate; orderID:" + orderID);
            sender.sendSuccessOrchestration(orderID, warehouseStockEntity.get().getCost());
            return;
        }
        sender.log("[WarehouseService::createOrderOrchestration] no stock found for order type, reject; orderID:" + orderID);
        sender.sendFailureOrchestration(orderID);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createOrderChoreography(Integer orderID, Integer orderType) {
        sender.log("[WarehouseService::createOrderChoreography] start; orderID:" + orderID);
        Optional<WarehouseStockVersionFileEntity> existingVersionFile = warehouseStockVersionFileRepository.findByOrderID(orderID);
        if (existingVersionFile.isPresent() && existingVersionFile.get().getStatus() == WarehouseStockStatusEnum.REJECTED) {
            sender.log("[WarehouseService::createOrderChoreography] already rejected, do nothing; orderID:" + orderID);
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
            warehouseStockVersionFileRepository.save(versionFile);

            sender.log("[WarehouseService::createOrderChoreography] stock found and reduced, validate; orderID:" + orderID);
            sender.sendSuccessChoreography(orderID, warehouseStockEntity.get().getCost());
            return;
        }
        sender.log("[WarehouseService::createOrderChoreography] no stock found for order type, reject; orderID:" + orderID);
        sender.sendFailureChoreography(orderID);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void validateOrder(Integer orderID, Integer orderType, boolean validated) {
        sender.log("[WarehouseService::validateOrder] start; orderID:" + orderID);
        Optional<WarehouseStockVersionFileEntity> existingVersionFile = warehouseStockVersionFileRepository.findByOrderID(orderID);
        if (existingVersionFile.isPresent()) {
            if (validated) {
                sender.log("[WarehouseService::validateOrder] validated reservation, finalize it; orderID:" + orderID);
                existingVersionFile.get().setStatus(WarehouseStockStatusEnum.FINALIZED);
                warehouseStockVersionFileRepository.save(existingVersionFile.get());
            }
            else {
                sender.log("[WarehouseService::validateOrder] accounting invalid, reject; orderID:" + orderID);
                existingVersionFile.get().setStatus(WarehouseStockStatusEnum.REJECTED);
                warehouseStockVersionFileRepository.save(existingVersionFile.get());

                Optional<WarehouseStockEntity> warehouseStockEntity = warehouseStockRepository.findByOrderType(existingVersionFile.get().getOrderType());
                warehouseStockEntity.get().setStock(warehouseStockEntity.get().getStock() + 1);
                warehouseStockRepository.save(warehouseStockEntity.get());
                sender.log("[WarehouseService::validateOrder] accounting invalid, rejected and saved; orderID:" + orderID);
            }
        }
        else {
            //if not present, add a version file entity, so that when the message from order comes, we can reject it
            //should only return invalid here, as accounting shouldn't finalize without our message first
            WarehouseStockVersionFileEntity versionFile = new WarehouseStockVersionFileEntity();
            versionFile.setOrderID(orderID);
            if (orderType != null) {
                versionFile.setOrderType(orderType);
            }
            versionFile.setStatus(validated ? WarehouseStockStatusEnum.FINALIZED : WarehouseStockStatusEnum.REJECTED);
            warehouseStockVersionFileRepository.save(versionFile);
            sender.log("[WarehouseService::validateOrder] create version file; orderID:" + orderID + " status:" + versionFile.getStatus());
        }
    }
}
