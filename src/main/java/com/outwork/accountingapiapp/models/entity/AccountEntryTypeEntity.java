package com.outwork.accountingapiapp.models.entity;

import com.outwork.accountingapiapp.constants.DataConstraint;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "account_entry_types")
public class AccountEntryTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = DataConstraint.ID_STRING_MAX_LENGTH)
    private String title;
}
