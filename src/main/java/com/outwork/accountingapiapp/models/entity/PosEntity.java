package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.outwork.accountingapiapp.constants.DataConstraint;
import com.outwork.accountingapiapp.constants.PosStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "pos",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"accountNumber", "bank"})}
)
public class PosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, updatable = false, length = DataConstraint.ENTITY_CODE_MAX_LENGTH)
    private String code;

    @Column(unique = true, nullable = false, length = DataConstraint.SHORT_STRING_MAX_LENGTH)
    private String name;

    @Enumerated(EnumType.STRING)
    private PosStatusEnum posStatus;

    @Column
    private String address;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String accountNumber;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String bank;

    @Column
    private int maxBillAmount;

    @OneToMany(mappedBy = "pos", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<PosCardFeeEntity> supportedCardTypes;

    @JsonIgnore
    @OneToMany(mappedBy = "pos", fetch = FetchType.LAZY)
    private List<BillEntity> bills;

    public void setSupportedCardTypes(@NotNull List<PosCardFeeEntity> posCardFees) {
        this.supportedCardTypes = posCardFees.stream().peek(fee -> fee.setPos(this)).toList();
    }
}
