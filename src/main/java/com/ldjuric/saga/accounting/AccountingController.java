package com.ldjuric.saga.accounting;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounting")
@CrossOrigin(origins="*", maxAge=3600)
public class AccountingController {

    private final AccountingService accountingService;
}
