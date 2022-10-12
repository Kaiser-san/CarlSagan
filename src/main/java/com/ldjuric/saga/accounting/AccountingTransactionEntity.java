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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "accountingID")
    private AccountingEntity accountingEntity;

    @Column(name = "orderID")
    private Integer orderID;

    @Column(name = "orderType")
    private Integer orderType;

    @Column(name = "warehouseReservationID")
    private Integer warehouseReservationID;

    @Column(name = "cost")
    private Integer cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountingTransactionStatusEnum status;
}