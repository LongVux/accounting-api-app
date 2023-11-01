package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "customer_cards",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"accountNumber", "bank"})}
)
public class CustomerCardEntity extends Auditable<String> {
    public static final String FIELD_NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "cardTypeId", nullable = false)
    private CardTypeEntity cardType;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String accountNumber;

    @Column(nullable = false, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String bank;

    private int paymentLimit;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDueDate;

    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId", nullable = false, updatable = false)
    private CustomerEntity customer;

    @JsonIgnore
    @OneToMany(mappedBy = "customerCard", fetch = FetchType.LAZY)
    private List<ReceiptEntity> receipts;
}
