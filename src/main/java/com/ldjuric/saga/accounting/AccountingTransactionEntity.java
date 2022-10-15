package com.ldjuric.saga.accounting;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "accounting_transaction")
public class AccountingTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "accounting_id")
    private AccountingEntity accountingEntity;

    @Column(name = "order_id")
    private Integer orderID;

    @Column(name = "order_type")
    private Integer orderType;

    @Column(name = "cost")
    private Integer cost;
}