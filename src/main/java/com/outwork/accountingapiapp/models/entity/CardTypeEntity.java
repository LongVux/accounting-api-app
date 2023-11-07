package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "card_types")
public class CardTypeEntity {
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "cardType", fetch = FetchType.LAZY)
    private List<CustomerCardEntity> customerCards;

    @JsonIgnore
    @OneToMany(mappedBy = "cardType", fetch = FetchType.LAZY)
    private List<PosCardFeeEntity> supportedPosIds;
}
