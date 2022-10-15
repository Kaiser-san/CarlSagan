package com.ldjuric.saga.accounting;

import lombok.Data;
import org.springframework.context.annotation.Profile;

import javax.persistence.*;

@Profile({"accounting", "all"})
@Entity
@Data
@Table(name = "accounting")
public class AccountingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "credit")
    private Integer credit;
}
