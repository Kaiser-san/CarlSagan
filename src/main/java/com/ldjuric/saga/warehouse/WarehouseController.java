package com.ldjuric.saga.warehouse;

import com.ldjuric.saga.interfaces.WarehouseServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse")
@CrossOrigin(origins="*", maxAge=3600)
public class WarehouseController {
    @Autowired
    private WarehouseServiceInterface warehouseService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseName(@PathVariable("id") Integer id) {
        String result = warehouseService.getWarehouseName(id);
        if(!result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid warehouse");
    }
}
