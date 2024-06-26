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
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_PHONE_NUMBER = "phoneNumber";
    public static final String FIELD_NATIONAL_ID = "nationalId";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;

    private String address;

    @Column(unique = true, nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String phoneNumber;

    @Column(unique = true, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String nationalId;

    @Column
    private double percentageFee;

    private String note;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<CustomerCardEntity> customerCards;
}
