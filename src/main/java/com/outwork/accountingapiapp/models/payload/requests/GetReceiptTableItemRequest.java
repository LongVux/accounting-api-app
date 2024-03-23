package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.ReceiptSortingEnum;
import com.outwork.accountingapiapp.constants.ReceiptStatusEnum;
import com.outwork.accountingapiapp.models.entity.*;
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
    private String customerName;

    @Nullable
    private UUID customerCardId;

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
    private String createdBy;

    @Nullable
    private List<ReceiptStatusEnum> receiptStatusList;

    @Nullable
    private List<String> branchCodes;

    @Nullable
    private Boolean OnlyNotConfirmedReceipt;


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
                    String.format(DataFormat.LIKE_QUERY_FORMAT, employeeCode)
            ));
        }

        if (!ObjectUtils.isEmpty(fromCreatedDate) && !ObjectUtils.isEmpty(toCreatedDate)) {
            predicates.add(criteriaBuilder.between(
                    root
                            .get(ReceiptEntity.FIELD_CREATED_DATE),
                    DateTimeUtils.atStartOfDay(fromCreatedDate),
                    DateTimeUtils.atEndOfDay(toCreatedDate)
            ));
        }

        if (!ObjectUtils.isEmpty(receiptCode)) {
            predicates.add(criteriaBuilder.like(
                    root.get(ReceiptEntity.FIELD_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, receiptCode)
            ));
        }

        if (!ObjectUtils.isEmpty(customerName)) {
            predicates.add(criteriaBuilder.like(
                    root
                            .get(ReceiptEntity.FIELD_CUSTOMER_CARD)
                            .get(CustomerCardEntity.FIELD_CUSTOMER)
                            .get(CustomerEntity.FIELD_NAME),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, customerName)
            ));
        }

        if (!ObjectUtils.isEmpty(customerCardId)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(ReceiptEntity.FIELD_CUSTOMER_CARD)
                            .get(CustomerCardEntity.FIELD_ID),
                    customerCardId
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

        if (!ObjectUtils.isEmpty(branchCodes)) {
            predicates.add(root.get(ReceiptEntity.FIELD_BRANCH).get(BranchEntity.FIELD_CODE).in(branchCodes));
        }

        if (!ObjectUtils.isEmpty(createdBy)) {
            predicates.add(criteriaBuilder.equal(
                    root
                            .get(ReceiptEntity.FIELD_CREATED_BY),
                    createdBy
            ));
        }

        if (Boolean.TRUE.equals(getOnlyNotConfirmedReceipt())) {
            predicates.add(criteriaBuilder.isNull(
                    root.get(ReceiptEntity.FIELD_CODE)
            ));
        }

        // return a conjunction of all predicates
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    Map<ReceiptSortingEnum, String> getSorterMap() {
        return MapBuilder.buildReceiptTableItemSorter();
    }
}
