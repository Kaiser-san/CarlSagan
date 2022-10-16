package com.ldjuric.saga.warehouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile({"warehouse", "all"})
@RestController
@RequestMapping("/warehouse")
@CrossOrigin(origins="*", maxAge=3600)
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping()
    public ResponseEntity<?> getWarehouse() {
        String result = warehouseService.getWarehouseStock();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{orderType}")
    public ResponseEntity<?> getWarehouseStock(@PathVariable("orderType") Integer orderType) {
        String result = warehouseService.getWarehouseStock(orderType);
        if(!result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid orderType");
    }

    @GetMapping("/versionFile")
    public ResponseEntity<?> getWarehouseVersionFiles() {
        String result = warehouseService.getWarehouseVersionFile();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/{orderType}/{cost}/{stock}")
    public ResponseEntity<?> createWarehouseStock(@PathVariable("orderType") Integer orderType, @PathVariable("cost") Integer cost, @PathVariable("stock") Integer stock) {
        boolean result = warehouseService.createWarehouseStock(orderType, cost, stock);
        if(result)
            return ResponseEntity.status(HttpStatus.OK).body("");
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("OrderType already exists");
    }
}
