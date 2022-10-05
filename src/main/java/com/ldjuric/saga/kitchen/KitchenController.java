package com.ldjuric.saga.kitchen;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins="*", maxAge=3600)
public class KitchenController {
    private final KitchenService kitchenService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getKitchenName(@PathVariable("id") Integer id) {
        String result = kitchenService.getKitchenName(id);
        if(!result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid kitchen");
    }
}
