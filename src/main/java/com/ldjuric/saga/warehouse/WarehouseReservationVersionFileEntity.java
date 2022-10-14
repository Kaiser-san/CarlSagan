package com.ldjuric.saga.warehouse;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "warehouse_reservation_version_file")
public class WarehouseReservationVersionFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id")
    private Integer orderID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WarehouseReservationStatusEnum status;
}
