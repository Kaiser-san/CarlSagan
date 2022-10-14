package com.ldjuric.saga.accounting;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "accounting_transaction_version_file")
public class AccountingTransactionVersionFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "accounting_id")
    private AccountingEntity accountingEntity;

    @Column(name = "order_id")
    private Integer orderID;

    @Column(name = "order_type")
    private Integer orderType;

    @Column(name = "warehouse_reservation_id")
    private Integer warehouseReservationID;

    @Column(name = "cost")
    private Integer cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountingTransactionStatusEnum status;
}
