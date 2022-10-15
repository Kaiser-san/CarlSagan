package com.ldjuric.saga.warehouse;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "warehouse_stock_version_file")
public class WarehouseStockVersionFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Integer orderID;

    @Column(name = "order_type", nullable = false, unique = true)
    private Integer orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WarehouseStockStatusEnum status;
}
