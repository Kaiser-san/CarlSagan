package com.ldjuric.saga.warehouse;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "warehouse")
public class WarehouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "cost")
    private Integer cost;
}