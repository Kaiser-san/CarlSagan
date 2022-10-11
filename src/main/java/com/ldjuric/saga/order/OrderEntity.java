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

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "kitchen_appointments_id")
    private Integer kitchen_appointments_id;

    @Column(name = "accounting_transactions_id")
    private Integer accounting_transactions_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatusEnum status;
}