package com.ldjuric.saga.accounting;

import lombok.Data;
import org.springframework.context.annotation.Profile;

import javax.persistence.*;

@Profile({"accounting", "all"})
@Entity
@Data
@Table(name = "accounting_transaction_version_file")
public class AccountingTransactionVersionFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "accounting_id")
    private AccountingEntity accountingEntity;

    @Column(name = "order_id")
    private Integer orderID;

    @Column(name = "order_type")
    private Integer orderType;

    @Column(name = "cost")
    private Integer cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountingTransactionStatusEnum status;
}
