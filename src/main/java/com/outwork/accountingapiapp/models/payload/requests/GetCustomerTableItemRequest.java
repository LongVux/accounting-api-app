package com.outwork.accountingapiapp.models.payload.requests;

import com.outwork.accountingapiapp.constants.CustomerSortingEnum;
import com.outwork.accountingapiapp.constants.DataFormat;
import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.responses.CustomerTableItem;
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
public class GetCustomerTableItemRequest extends SortedPagination<CustomerSortingEnum> implements Specification<CustomerTableItem> {
    @Nullable
    private String name;

    @Nullable
    private String phoneNumber;

    @Nullable
    private String nationalId;


    @Override
    Map<CustomerSortingEnum, String> getSorterMap() {
        return MapBuilder.buildCustomerTableItemSorter();
    }

    @Override
    public Predicate toPredicate(Root<CustomerTableItem> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!ObjectUtils.isEmpty(name)) {
            predicates.add(criteriaBuilder.like(
                    root.get(CustomerEntity.FIELD_NAME),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, name)
            ));
        }

        if (!ObjectUtils.isEmpty(phoneNumber)) {
            predicates.add(criteriaBuilder.like(
                    root.get(CustomerEntity.FIELD_PHONE_NUMBER),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, phoneNumber)
            ));
        }

        if (!ObjectUtils.isEmpty(nationalId)) {
            predicates.add(criteriaBuilder.like(
                    root.get(CustomerEntity.FIELD_NATIONAL_ID),
                    String.format(DataFormat.LIKE_QUERY_FORMAT, nationalId)
            ));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
