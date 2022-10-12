package com.ldjuric.saga.warehouse;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "warehouse_reservations")
public class WarehouseReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouseID")
    private WarehouseEntity warehouse;

    @Column(name = "orderID")
    private Integer orderID;

    @Column(name = "orderType")
    private Integer orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WarehouseReservationStatusEnum status;
}
