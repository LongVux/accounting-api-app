package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.BillSortingEnum;
import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.models.entity.BillEntity;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.responses.BillTableItem;
import com.outwork.accountingapiapp.utils.MapBuilder;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GetBillTableItemRequest extends SortedPagination<BillSortingEnum> implements Specification<BillTableItem> {
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fromCreatedDate;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date toCreatedDate;

    @Nullable
    private String code;

    @Nullable
    private String posCode;

    @Nullable
    private String receiptCode;

    @Nullable
    private Double fromMoneyAmount;

    @Nullable
    private Double toMoneyAmount;

    @Nullable
    private Double fromFee;

    @Nullable
    private Double toFee;

    @Nullable
    private Double fromEstimatedProfit;

    @Nullable
    private Double toEstimatedProfit;

    @Nullable
    private Double fromReturnedProfit;

    @Nullable
    private Double toReturnedProfit;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fromReturnedTime;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date toReturnedTime;

    @Override
    Map<BillSortingEnum, String> getSorterMap() {
        return MapBuilder.buildBillTableItemSorter();
    }

    @Override
    public Predicate toPredicate(Root<BillTableItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(fromCreatedDate) && !ObjectUtils.isEmpty(toCreatedDate)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BillEntity.FIELD_CREATED_DATE),
                    fromCreatedDate,
                    toCreatedDate
            ));
        }

        if (!ObjectUtils.isEmpty(code)) {
            predicates.add(criteriaBuilder.like(
                    root.get(BillEntity.FIELD_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, code)
            ));
        }

        if (!ObjectUtils.isEmpty(posCode)) {
            predicates.add(criteriaBuilder.like(
                    root.get(BillEntity.FIELD_POS).get(PosEntity.FIELD_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, posCode)
            ));
        }

        if (!ObjectUtils.isEmpty(receiptCode)) {
            predicates.add(criteriaBuilder.like(
                    root.get(BillEntity.FIELD_RECEIPT).get(ReceiptEntity.FIELD_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, receiptCode)
            ));
        }

        if (!ObjectUtils.isEmpty(fromMoneyAmount) && !ObjectUtils.isEmpty(toMoneyAmount)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BillEntity.FIELD_MONEY_AMOUNT),
                    fromMoneyAmount,
                    toMoneyAmount
            ));
        }

        if (!ObjectUtils.isEmpty(fromFee) && !ObjectUtils.isEmpty(toFee)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BillEntity.FIELD_FEE),
                    fromFee,
                    toFee
            ));
        }

        if (!ObjectUtils.isEmpty(fromEstimatedProfit) && !ObjectUtils.isEmpty(toEstimatedProfit)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BillEntity.FIELD_ESTIMATED_PROFIT),
                    fromEstimatedProfit,
                    toEstimatedProfit
            ));
        }

        if (!ObjectUtils.isEmpty(fromReturnedProfit) && !ObjectUtils.isEmpty(toReturnedProfit)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BillEntity.FIELD_RETURNED_PROFIT),
                    fromReturnedProfit,
                    toReturnedProfit
            ));
        }

        if (!ObjectUtils.isEmpty(fromReturnedTime) && !ObjectUtils.isEmpty(toReturnedTime)) {
            predicates.add(criteriaBuilder.between(
                    root.get(BillEntity.FIELD_RETURNED_TIME),
                    fromCreatedDate,
                    toCreatedDate
            ));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
