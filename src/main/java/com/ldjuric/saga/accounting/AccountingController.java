package com.ldjuric.saga.accounting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile({"accounting", "all"})
@RestController
@RequestMapping("/accounting")
@CrossOrigin(origins="*", maxAge=3600)
public class AccountingController {
    @Autowired
    private AccountingService accountingService;

    @GetMapping()
    public ResponseEntity<?> getAccounts() {
        String result = accountingService.getAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getAccount(@PathVariable("username") String username) {
        String result = accountingService.getAccount(username);
        if(!result.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(result);
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid orderType");
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions() {
        String result = accountingService.getTransactions();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/versionFile")
    public ResponseEntity<?> getAccountVersionFiles() {
        String result = accountingService.getVersionFiles();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/{username}/{credit}")
    public ResponseEntity<?> createAccount(@PathVariable("username") String username, @PathVariable("credit") Integer credit) {
        boolean result = accountingService.createAccount(username, credit);
        if(result)
            return ResponseEntity.status(HttpStatus.OK).body("");
        else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("OrderType already exists");
    }
}
