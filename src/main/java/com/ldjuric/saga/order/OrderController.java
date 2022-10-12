package com.ldjuric.saga.order;

import com.ldjuric.saga.logs.interfaces.OrderServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins="*", maxAge=3600)

public class OrderController {
    @Autowired
    private OrderServiceInterface orderService;

    @Autowired
    private OrderMQSender orderSender;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        String result = orderService.getOrder(id);
        if(!result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user");
    }

    @PutMapping("/orchestration/create/{orderType}/{username}/{password}")
    public ResponseEntity<?> orchestrationCreate(@PathVariable("orderType") Integer orderType, @PathVariable("username") String username, @PathVariable("password") String password) {
        orderService.orchestrationCreate(orderType, username, password);
        return ResponseEntity.status(HttpStatus.OK).body("Request sent");
    }

    @PutMapping("/choreography/create/{orderType}/{username}/{password}")
    public ResponseEntity<?> choreographyCreate(@PathVariable("orderType") Integer orderType, @PathVariable("username") String username, @PathVariable("password") String password) {
        Integer orderID = orderService.choreographyCreate(orderType, username, password);
        orderSender.sendChoreography(orderID, orderType, username, password);
        return ResponseEntity.status(HttpStatus.OK).body("Request sent");
    }
}
