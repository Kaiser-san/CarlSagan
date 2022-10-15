package com.ldjuric.saga.accounting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"accounting", "all"})
@RestController
@RequestMapping("/accounting")
@CrossOrigin(origins="*", maxAge=3600)
public class AccountingController {
    @Autowired
    private AccountingService accountingService;
}
