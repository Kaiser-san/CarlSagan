package com.ldjuric.saga.kitchen;

import com.ldjuric.saga.interfaces.KitchenServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins="*", maxAge=3600)
public class KitchenController {
    @Autowired
    private KitchenServiceInterface kitchenService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getKitchenName(@PathVariable("id") Integer id) {
        String result = kitchenService.getKitchenName(id);
        if(!result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid kitchen");
    }
}
