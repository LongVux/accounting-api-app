package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.ReceiptSortingEnum;
import com.outwork.accountingapiapp.constants.ReceiptStatusEnum;
import com.outwork.accountingapiapp.models.entity.CustomerCardEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.entity.UserEntity;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetReceiptTableItemRequest extends SortedPagination<ReceiptSortingEnum> implements Specification<ReceiptEntity> {
    @Nullable
    private UUID employeeId;

    @Nullable
    private String employeeCode;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fromCreatedDate;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date toCreatedDate;

    @Nullable
    private String receiptCode;

    @Nullable
    private String cardName;

    @Nullable
    private Double fromTransactionTotal;

    @Nullable
    private Double toTransactionTotal;

    @Nullable
    private Integer fromIntake;

    @Nullable
    private Integer toIntake;

    @Nullable
    private Integer fromPayout;

    @Nullable
    private Integer toPayout;

    @Nullable
    private Integer fromLoan;

    @Nullable
    private Integer toLoan;

    @Nullable
    private Integer fromRepayment;

    @Nullable
    private Integer toRepayment;

    @Nullable
    private Double fromEstimatedProfit;

    @Nullable
    private Double toEstimatedProfit;

    @Nullable
    private Double fromCalculatedProfit;

    @Nullable
    private Double toCalculatedProfit;

    @Nullable
    private List<ReceiptStatusEnum> receiptStatusList;

    @Override
    public Predicate toPredicate(Root<ReceiptEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(employeeId)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(ReceiptEntity.FIELD_EMPLOYEE)
                            .get(UserEntity.FIELD_ID),
                    employeeId
            ));
        }

        if (!ObjectUtils.isEmpty(employeeCode)) {
            predicates.add(criteriaBuilder.like(
                    root
                            .get(ReceiptEntity.FIELD_EMPLOYEE)
                            .get(UserEntity.FIELD_CODE),
                    employeeCode
            ));
        }

        if (!ObjectUtils.isEmpty(fromCreatedDate) && !ObjectUtils.isEmpty(toCreatedDate)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_CREATED_DATE),
                    fromCreatedDate,
                    toCreatedDate
            ));
        }

        if (!ObjectUtils.isEmpty(receiptCode)) {
            predicates.add(criteriaBuilder.like(
                    root
                            .get(ReceiptEntity.FIELD_CODE),
                    receiptCode
            ));
        }

        if (!ObjectUtils.isEmpty(cardName)) {
            predicates.add(criteriaBuilder.like(
                    root
                            .get(ReceiptEntity.FIELD_CUSTOMER_CARD)
                            .get(CustomerCardEntity.FIELD_NAME),
                    cardName
            ));
        }

        if (!ObjectUtils.isEmpty(fromTransactionTotal) && !ObjectUtils.isEmpty(toTransactionTotal)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_TRANSACTION_TOTAL),
                    fromTransactionTotal,
                    toTransactionTotal
            ));
        }

        if (!ObjectUtils.isEmpty(fromIntake) && !ObjectUtils.isEmpty(toIntake)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_INTAKE),
                    fromIntake,
                    toIntake
            ));
        }

        if (!ObjectUtils.isEmpty(fromPayout) && !ObjectUtils.isEmpty(toPayout)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_PAYOUT),
                    fromPayout,
                    toPayout
            ));
        }

        if (!ObjectUtils.isEmpty(fromLoan) && !ObjectUtils.isEmpty(toLoan)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_LOAN),
                    fromLoan,
                    toLoan
            ));
        }

        if (!ObjectUtils.isEmpty(fromRepayment) && !ObjectUtils.isEmpty(toRepayment)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_REPAYMENT),
                    fromRepayment,
                    toRepayment
            ));
        }

        if (!ObjectUtils.isEmpty(fromEstimatedProfit) && !ObjectUtils.isEmpty(toEstimatedProfit)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_ESTIMATED_PROFIT),
                    fromEstimatedProfit,
                    toEstimatedProfit
            ));
        }

        if (!ObjectUtils.isEmpty(fromCalculatedProfit) && !ObjectUtils.isEmpty(toCalculatedProfit)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_CALCULATED_PROFIT),
                    fromCalculatedProfit,
                    toCalculatedProfit
            ));
        }

        if (!CollectionUtils.isEmpty(receiptStatusList)) {
            predicates.add(root.get(ReceiptEntity.FIELD_RECEIPT_STATUS).in(receiptStatusList));
        }

        // return a conjunction of all predicates
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    Map<ReceiptSortingEnum, String> getSorterMap() {
        return MapBuilder.buildReceiptTableItemSorter();
    }
}
