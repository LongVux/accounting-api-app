package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "customers")
public class CustomerEntity extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;

    private String address;

    @Column(unique = true, nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String phoneNumber;

    @Column(unique = true, nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String nationalId;

    @Column(nullable = false)
    private int percentageFee;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CustomerCardEntity> customerCards;
}
