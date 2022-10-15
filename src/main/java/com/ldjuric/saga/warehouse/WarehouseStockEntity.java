package com.ldjuric.saga.warehouse;

import lombok.Data;
import org.springframework.context.annotation.Profile;

import javax.persistence.*;

@Profile({"warehouse", "all"})
@Entity
@Data
@Table(name = "warehouse_stock")
public class WarehouseStockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_type", nullable = false, unique = true)
    private Integer orderType;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "stock")
    private Integer stock;
}