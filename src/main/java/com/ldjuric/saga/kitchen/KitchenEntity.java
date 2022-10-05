package com.ldjuric.saga.kitchen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ldjuric.saga.order.OrderStatusEnum;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "kitchen")
public class KitchenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "cost")
    private Integer cost;
}