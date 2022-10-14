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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
