package com.ldjuric.saga.accounting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ldjuric.saga.order.OrderStatusEnum;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "accounting")
public class AccountingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "credit")
    private Integer credit;
}
