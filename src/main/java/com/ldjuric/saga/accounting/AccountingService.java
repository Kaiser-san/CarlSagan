package com.ldjuric.saga.accounting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountingService {
    private final AccountingRepository accountingRepository;
    private final AccountingTransactionRepository accountingTransactionRepository;
}
