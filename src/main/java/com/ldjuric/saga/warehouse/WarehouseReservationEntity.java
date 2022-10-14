package com.ldjuric.saga.warehouse;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "warehouse_reservation")
public class WarehouseReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;

    @Column(name = "order_id")
    private Integer orderID;

    @Column(name = "order_type")
    private Integer orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WarehouseReservationStatusEnum status;
}
