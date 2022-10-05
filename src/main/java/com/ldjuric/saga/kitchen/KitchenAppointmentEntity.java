package com.ldjuric.saga.kitchen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "kitchen_appointment")
public class KitchenAppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "kitchen_id")
    private KitchenEntity kitchen;

    @Column(name = "order_id")
    private Integer orderID;

    @Column(name = "order_type")
    private Integer orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private KitchenAppointmentStatusEnum status;
}
