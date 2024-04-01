package com.outwork.accountingapiapp.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "user_branch")
@AllArgsConstructor
@NoArgsConstructor
public class UserBranchEntity {
    public static final String FIELD_BRANCH = "branch";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branchId", nullable = false)
    private BranchEntity branch;

    @Column(nullable = false)
    private int orderId;

    public UserBranchEntity (UserEntity user, BranchEntity branch, int orderId) {
        this.user = user;
        this.branch = branch;
        this.orderId = orderId;
    }
}
