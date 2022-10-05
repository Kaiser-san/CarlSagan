package com.ldjuric.saga.accounting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "kitchen_transaction")
public class AccountingTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "accounting_id")
    private AccountingEntity accountingEntity;

    @Column(name = "order_id")
    private Integer orderID;

    @Column(name = "username")
    private String username;

    @Column(name = "kitchen_appointment_id")
    private Integer kitchenAppointmentID;

    @Column(name = "order_type")
    private Integer orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountingTransactionStatusEnum status;
}