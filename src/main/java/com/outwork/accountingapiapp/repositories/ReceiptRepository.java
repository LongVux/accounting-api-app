package com.outwork.accountingapiapp.repositories;

import com.outwork.accountingapiapp.models.entity.BranchEntity;
import com.outwork.accountingapiapp.models.entity.ReceiptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, UUID> {
    Optional<ReceiptEntity> findFirstByCodeNotNullAndBranchAndCreatedDateBetweenOrderByCreatedDateDesc(
            BranchEntity branch,
            Date createdDateStart,
            Date createdDateEnd
    );

    Page<ReceiptEntity> findAll (Specification<ReceiptEntity> receiptSpecification, Pageable pageable);
}
