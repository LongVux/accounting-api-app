package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.*;
import com.outwork.accountingapiapp.models.entity.GeneralAccountEntryEntity;
import com.outwork.accountingapiapp.models.payload.responses.GeneralAccountEntryTableItem;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetGeneralAccountEntryTableItemRequest extends SortedPagination<GeneralAccountEntrySortingEnum> implements Specification<GeneralAccountEntryEntity> {
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


    @Override
    Map<GeneralAccountEntrySortingEnum, String> getSorterMap() {
        return MapBuilder.buildGeneralAccountTableItemSorter();
    }

    @Override
    public Predicate toPredicate(Root<GeneralAccountEntryEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(fromCreatedDate) && !ObjectUtils.isEmpty(toCreatedDate)) {
            predicates.add(criteriaBuilder.between(
                    root.get(GeneralAccountEntryEntity.FIELD_CREATED_DATE),
                    DateTimeUtils.atStartOfDay(fromCreatedDate),
                    DateTimeUtils.atEndOfDay(toCreatedDate)
            ));
        }

        if (!ObjectUtils.isEmpty(entryCode)) {
            predicates.add(criteriaBuilder.like(
                    root.get(GeneralAccountEntryEntity.FIELD_ENTRY_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, entryCode)
            ));
        }

        if (!ObjectUtils.isEmpty(entryType)) {
            predicates.add(criteriaBuilder.like(
                    root.get(GeneralAccountEntryEntity.FIELD_ENTRY_TYPE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, entryType)));
        }

        if (!ObjectUtils.isEmpty(entryStatusList)) {
            predicates.add(root.get(GeneralAccountEntryEntity.FIELD_ENTRY_STATUS).in(entryStatusList));
        }

        if (!ObjectUtils.isEmpty(transactionTypes)) {
            predicates.add(root.get(GeneralAccountEntryEntity.FIELD_TRANSACTION_TYPE).in(transactionTypes));
        }

        if (!ObjectUtils.isEmpty(fromMoneyAmount) && !ObjectUtils.isEmpty(toMoneyAmount)) {
            predicates.add(criteriaBuilder.between(
                    root.get(GeneralAccountEntryEntity.FIELD_MONEY_AMOUNT),
                    fromMoneyAmount,
                    toMoneyAmount
            ));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
