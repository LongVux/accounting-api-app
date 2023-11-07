package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.CustomerEntity;
import com.outwork.accountingapiapp.models.payload.responses.CustomerTableItem;
import com.outwork.accountingapiapp.models.payload.responses.SuggestedCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    boolean existsByNationalIdAndIdNot(String nationalId, UUID id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID id);

    List<SuggestedCustomer> findByNameLikeIgnoreCase(String name);

    Page<CustomerTableItem> findAll (Specification<CustomerTableItem> specification, Pageable pageable);
}
