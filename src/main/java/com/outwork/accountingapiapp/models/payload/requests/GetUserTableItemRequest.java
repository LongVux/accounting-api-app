package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.UserSortingEnum;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.RoleEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
import com.outwork.accountingapiapp.utils.MapBuilder;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserTableItemRequest extends SortedPagination<UserSortingEnum> implements Specification<UserEntity> {
    @Nullable
    private UUID id;

    @Nullable
    private String code;

    @Nullable
    private String name;

    @Nullable
    private String email;

    @Nullable
    private String phoneNumber;

    @Nullable
    private String branchCode;

    @Nullable
    private String roleTitle;

    @Nullable
    private Double fromAccountBalance;

    @Nullable
    private Double toAccountBalance;


    @Override
    Map<UserSortingEnum, String> getSorterMap() {
        return MapBuilder.buildUserTableItemSorter();
    }

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(id)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(UserEntity.FIELD_ID),
                    id
            ));
        }

        if (!ObjectUtils.isEmpty(code)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(UserEntity.FIELD_CODE),
                    code
            ));
        }

        if (!ObjectUtils.isEmpty(name)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(UserEntity.FIELD_NAME),
                    name
            ));
        }

        if (!ObjectUtils.isEmpty(email)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(UserEntity.FIELD_EMAIL),
                    email
            ));
        }

        if (!ObjectUtils.isEmpty(phoneNumber)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(UserEntity.FIELD_PHONE_NUMBER),
                    phoneNumber
            ));
        }

        if (!ObjectUtils.isEmpty(branchCode)) {
            predicates.add(criteriaBuilder.like(
                    root.join (UserEntity.FIELD_BRANCHES).get(BranchEntity.FIELD_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, branchCode)
            ));
        }

        if (!ObjectUtils.isEmpty(roleTitle)) {
            predicates.add(criteriaBuilder.like(
                    root.join (UserEntity.FIELD_ROLES).get(RoleEntity.FIELD_TITLE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, roleTitle)
            ));
        }

        if (!ObjectUtils.isEmpty(fromAccountBalance) && !ObjectUtils.isEmpty(toAccountBalance)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(UserEntity.FIELD_ACCOUNT_BALANCE),
                    fromAccountBalance,
                    toAccountBalance
            ));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
