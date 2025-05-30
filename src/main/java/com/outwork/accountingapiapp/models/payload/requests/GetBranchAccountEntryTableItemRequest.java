package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.AccountEntryStatusEnum;
import com.outwork.accountingapiapp.constants.BranchAccountEntrySortingEnum;
import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.TransactionTypeEnum;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.utils.DateTimeUtils;
import com.outwork.accountingapiapp.utils.MapBuilder;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.ObjectUtils;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetBranchAccountEntryTableItemRequest extends SortedPagination<BranchAccountEntrySortingEnum> implements Specification<BranchAccountEntryEntity> {
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fromCreatedDate;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date toCreatedDate;

    @Nullable
    private String entryCode;

    @Nullable
    private String entryType;

    @Nullable
    private List<TransactionTypeEnum> transactionTypes;

    @Nullable
    private Double fromMoneyAmount;

    @Nullable
    private Double toMoneyAmount;

    @Nullable
    private List<AccountEntryStatusEnum> entryStatusList;

    @Nullable
    private List<String> branchCodes;

    @Nullable
    private String createdBy;

    @Override
    Map<BranchAccountEntrySortingEnum, String> getSorterMap() {
        return MapBuilder.buildBranchAccountTableItemSorter();
    }

    @Override
    public Predicate toPredicate(Root<BranchAccountEntryEntity> root, CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(fromCreatedDate) && !ObjectUtils.isEmpty(toCreatedDate)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BranchAccountEntryEntity.FIELD_CREATED_DATE),
                    DateTimeUtils.atStartOfDay(fromCreatedDate),
                    DateTimeUtils.atEndOfDay(toCreatedDate)
            ));
        }

        if (!ObjectUtils.isEmpty(entryCode)) {
            predicates.add(criteriaBuilder.like(
                    root.get(BranchAccountEntryEntity.FIELD_ENTRY_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, entryCode)
            ));
        }

        if (!ObjectUtils.isEmpty(entryType)) {
            predicates.add(criteriaBuilder.like(
                    root.get(BranchAccountEntryEntity.FIELD_ENTRY_TYPE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, entryType)
            ));
        }

        if (!ObjectUtils.isEmpty(entryStatusList)) {
            predicates.add(root.get(BranchAccountEntryEntity.FIELD_ENTRY_STATUS).in(entryStatusList));
        }

        if (!ObjectUtils.isEmpty(transactionTypes)) {
            predicates.add(root.get(BranchAccountEntryEntity.FIELD_TRANSACTION_TYPE).in(transactionTypes));
        }

        if (!ObjectUtils.isEmpty(fromMoneyAmount) && !ObjectUtils.isEmpty(toMoneyAmount)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BranchAccountEntryEntity.FIELD_MONEY_AMOUNT),
                    fromMoneyAmount,
                    toMoneyAmount
            ));
        }

        if (!ObjectUtils.isEmpty(branchCodes)) {
            predicates.add(root.get(BranchAccountEntryEntity.FIELD_BRANCH).get(BranchEntity.FIELD_CODE).in(branchCodes));
        }

        if (!ObjectUtils.isEmpty(createdBy)) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(
                            root.get(BranchAccountEntryEntity.FIELD_CREATED_BY),
                            String.format(DataFormat.LIKE_QUERY_FORMAT, createdBy)
                    ),
                    criteriaBuilder.like(
                            root.get(BranchAccountEntryEntity.FIELD_TARGET_EMPLOYEE),
                            String.format(DataFormat.LIKE_QUERY_FORMAT, createdBy)
                    )
            ));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
