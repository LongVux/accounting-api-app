package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.constants.PosSortingEnum;
import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.PosEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import com.outwork.accountingapiapp.models.payload.responses.PosTableItem;
import com.outwork.accountingapiapp.utils.MapBuilder;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetPosTableItemRequest extends SortedPagination<PosSortingEnum> implements Specification<PosTableItem> {
    @Nullable
    private String code;

    @Nullable
    private String name;

    @Nullable
    private String accountNumber;

    @Nullable
    private String bank;

    @Nullable
    private Integer fromMaxBillAmount;

    @Nullable
    private Integer toMaxBillAmount;

    @Nullable
    private List<String> branchCodes;

    @Override
    Map<PosSortingEnum, String> getSorterMap() {
        return MapBuilder.buildPosTableItemSorter();
    }

    @Override
    public Predicate toPredicate(Root<PosTableItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(code)) {
            predicates.add(criteriaBuilder.like(
                    root.get(PosEntity.FIELD_CODE),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, code)
            ));
        }

        if (!ObjectUtils.isEmpty(name)) {
            predicates.add(criteriaBuilder.like(
                    root.get(PosEntity.FIELD_NAME),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, name)
            ));
        }

        if (!ObjectUtils.isEmpty(accountNumber)) {
            predicates.add(criteriaBuilder.like(
                    root.get(PosEntity.FIELD_ACCOUNT_NUMBER),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, accountNumber)
            ));
        }

        if (!ObjectUtils.isEmpty(bank)) {
            predicates.add(criteriaBuilder.like(
                    root.get(PosEntity.FIELD_BANK),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, bank)
            ));
        }

        if (!ObjectUtils.isEmpty(fromMaxBillAmount) && !ObjectUtils.isEmpty(toMaxBillAmount)) {
            predicates.add(criteriaBuilder.between(
                    root.get(PosEntity.FIELD_MAX_BILL_AMOUNT),
                    fromMaxBillAmount,
                    toMaxBillAmount
            ));
        }

        if (!ObjectUtils.isEmpty(branchCodes)) {
            predicates.add(root.get(PosEntity.FIELD_BRANCHES).get(BranchEntity.FIELD_CODE).in(branchCodes));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
