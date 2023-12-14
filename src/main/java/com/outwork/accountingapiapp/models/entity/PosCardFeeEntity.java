package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "pos_card_fee")
public class PosCardFeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cartTypeId", nullable = false)
    private CardTypeEntity cardType;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "posId", nullable = false)
    private PosEntity pos;

    @Column
    private double posCardFee;
}
