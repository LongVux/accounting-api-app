package com.outwork.accountingapiapp.models.payload.requests;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Data
public abstract class SortedPagination<T extends Enum<T>> {
    @Min(0)
    private int page;

    @Min(1)
    private int pageSize;

    private T sorter;

    private Sort.Direction sortDirection;

    abstract Map<T, String> getSorterMap ();

    public Pageable retrievePageConfig() {
        Map<T, String> sortMap = getSorterMap();

        if (ObjectUtils.isEmpty(sortMap.get(sorter))) {
            return PageRequest.of(page, pageSize);
        }

        return PageRequest.of(page, pageSize, Sort.by(sortDirection, sortMap.get(sorter)));
    }
}
