package com.ldjuric.saga.order;

import lombok.Data;
import org.springframework.context.annotation.Profile;

import javax.persistence.*;

@Profile({"order", "all"})
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