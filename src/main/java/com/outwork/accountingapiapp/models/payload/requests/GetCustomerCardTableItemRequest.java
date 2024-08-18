package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.CustomerCardSortingEnum;
import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.models.entity.BranchAccountEntryEntity;
import com.outwork.accountingapiapp.models.entity.CardTypeEntity;
import com.outwork.accountingapiapp.models.entity.CustomerCardEntity;
import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.responses.CustomerCardTableItem;
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
public class GetCustomerCardTableItemRequest extends SortedPagination<CustomerCardSortingEnum> implements Specification<CustomerCardTableItem> {
    @Nullable
    private String customerName;

    @Nullable
    private String name;

    @Nullable
    private List<UUID> cardTypeIds;

    @Nullable
    private String accountNumber;

    @Nullable
    private String bank;

    @Nullable
    private String nationalId;

    @Nullable
    private Integer fromPaymentLimit;

    @Nullable
    private Integer toPaymentLimit;

    @Nullable
    private Integer fromPaymentDueDate;

    @Nullable
    private Integer toPaymentDueDate;

    @Nullable
    private Integer fromPrePaidFee;

    @Nullable
    private Integer toPrePaidFee;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fromCreatedDate;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date toCreatedDate;

    @Override
    Map<CustomerCardSortingEnum, String> getSorterMap() {
        return MapBuilder.buildCustomerCardTableItemSorter();
    }

    @Override
    public Predicate toPredicate(Root<CustomerCardTableItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(customerName)) {
            predicates.add(criteriaBuilder.like(
                    root
                            .get(CustomerCardEntity.FIELD_CUSTOMER)
                            .get(CustomerEntity.FIELD_NAME),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, customerName)
            ));
        }

        if (!ObjectUtils.isEmpty(name)) {
            predicates.add(criteriaBuilder.like(
                    root.get(CustomerCardEntity.FIELD_NAME),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, name)
            ));
        }

        if (!ObjectUtils.isEmpty(bank)) {
            predicates.add(criteriaBuilder.like(
                    root.get(CustomerCardEntity.FIELD_BANK),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, bank)
            ));
        }

        if (!ObjectUtils.isEmpty(accountNumber)) {
            predicates.add(criteriaBuilder.like(
                    root.get(CustomerCardEntity.FIELD_ACCOUNT_NUMBER),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, accountNumber)
            ));
        }

        if (!ObjectUtils.isEmpty(nationalId)) {
            predicates.add(criteriaBuilder.like(
                    root.get(CustomerCardEntity.FIELD_NATIONAL_ID),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, nationalId)
            ));
        }

        if (!ObjectUtils.isEmpty(fromPaymentLimit) && !ObjectUtils.isEmpty(toPaymentLimit)) {
            predicates.add(criteriaBuilder.between(
                    root.get(CustomerCardEntity.FIELD_PAYMENT_LIMIT),
                    fromPaymentLimit,
                    toPaymentLimit
            ));
        }

        if (!ObjectUtils.isEmpty(fromPaymentDueDate) && !ObjectUtils.isEmpty(toPaymentDueDate)) {
            predicates.add(criteriaBuilder.between(
                    root.get(CustomerCardEntity.FIELD_PAYMENT_DUE_DATE),
                    fromPaymentDueDate,
                    toPaymentDueDate
            ));
        }

        if (!ObjectUtils.isEmpty(fromPrePaidFee) && !ObjectUtils.isEmpty(toPrePaidFee)) {
            predicates.add(criteriaBuilder.between(
                    root.get(CustomerCardEntity.FIELD_PREPAID_FEE),
                    fromPrePaidFee,
                    toPrePaidFee
            ));
        }

        if (!ObjectUtils.isEmpty(cardTypeIds)) {
            predicates.add(
                    root.get(CustomerCardEntity.FIELD_CARD_TYPE).get(CardTypeEntity.FIELD_ID).in(cardTypeIds)
            );
        }

        if (!ObjectUtils.isEmpty(fromCreatedDate) && !ObjectUtils.isEmpty(toCreatedDate)) {
            predicates.add(criteriaBuilder.between(
                    root.get(CustomerCardEntity.FIELD_CREATED_DATE),
                    DateTimeUtils.atStartOfDay(fromCreatedDate),
                    DateTimeUtils.atEndOfDay(toCreatedDate)
            ));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
