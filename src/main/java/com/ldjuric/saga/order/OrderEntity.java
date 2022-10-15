package com.ldjuric.saga.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "order_table")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_type")
    private Integer orderType;

    @Column(name = "username")
    private String username;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "accounting_transaction_id")
    private Integer accountingTransactionID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatusEnum status;
}